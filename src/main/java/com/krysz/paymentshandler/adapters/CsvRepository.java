package com.krysz.paymentshandler.adapters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krysz.paymentshandler.domain.exceptions.CannotParseFileException;
import com.krysz.paymentshandler.domain.exceptions.DatabaseCorruptedError;
import com.krysz.paymentshandler.domain.exceptions.ResourceCannotBeFoundException;
import com.krysz.paymentshandler.domain.ports.PaymentRepository;
import com.krysz.paymentshandler.domain.ports.model.PaymentEntity;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.vavr.Tuple2;

public class CsvRepository implements PaymentRepository {
  private static final Logger logger = LoggerFactory.getLogger(CsvRepository.class);

  private static final String DEFAULT_DB_PATH = "./db.csv";

  private static File DATABASE_FILE;

  private final String canonicalDbPath;

  public CsvRepository(String storagePath) {
    DATABASE_FILE = new File(storagePath);
    canonicalDbPath = getCanonicalDbPath(storagePath);
    checkDbAvailability();
  }
  
  public CsvRepository() {
    DATABASE_FILE = new File(DEFAULT_DB_PATH);
    canonicalDbPath = getCanonicalDbPath(DEFAULT_DB_PATH);
    checkDbAvailability();
  }

  private String getCanonicalDbPath(String path) {
    final String canonicalDbPath;
    try {
      canonicalDbPath = DATABASE_FILE.getCanonicalPath();
    } catch (IOException ex) {
      throw new DatabaseCorruptedError("Database cannot be obtained, cannot get canonical path to: "
          + path, ex);
    }
    return canonicalDbPath;
  }

  private void checkDbAvailability() {
    if (DATABASE_FILE.exists() && DATABASE_FILE.isDirectory()) {
      throw new DatabaseCorruptedError("Database cannot be opened because the object of name: " + canonicalDbPath + " is a directory!");
    } else if (DATABASE_FILE.exists()) {
      checkConsistencyOfDb();
    } else {
      try {
        DATABASE_FILE.createNewFile();
      } catch (IOException ex) {
        throw new DatabaseCorruptedError("Database cannot be created: ", ex);
      }
    }
  }

  private void checkConsistencyOfDb() {
    Set<String> check = new HashSet<>();
    try (FileReader fileIn = new FileReader(canonicalDbPath);
        BufferedReader reader = new BufferedReader(fileIn)) {
      String line;
      while ((line = reader.readLine()) != null) {

        if(line.isBlank() || !check.add(line.substring(0, line.indexOf(',')))) {
         throw new DatabaseCorruptedError("Database is corrupted! There are duplicates or blank lines! Cannot proceed!");
        }
      }
    } catch (IOException ex) {
      logger.error("Cannot parse " + canonicalDbPath + " in order to check cosistency of DB: ", ex);
      throw new DatabaseCorruptedError("Database is corrupted! Cannot parse it to read or write! " + ex.getMessage());
    }
  }

  @Override
  public List<PaymentEntity> findAll() {
    try(Reader reader = Files.newBufferedReader(Path.of(canonicalDbPath))) {
      return new CsvToBeanBuilder<PaymentEntity>(reader)
          .withType(PaymentEntity.class)
          .build()
          .parse();
    } catch (IOException ex) {
      logger.error("Cannot parse the file " + canonicalDbPath + ": ", ex);
      throw new CannotParseFileException("Cannot read from file " + canonicalDbPath + ex.getMessage());
    }
  }

  @Override
  public PaymentEntity findById(Long id) {
    Tuple2<String, Integer> line = getLineStartsWithId(id);
    if (line._1 != null) {
      return mapLineIntoEntity(line._1);
    }
    return null;
  }

  @Override
  public PaymentEntity save(PaymentEntity entity) {
    if (entity.getId() != null) {
      throw new UnsupportedOperationException("Cannot save the entity with set ID! Use update instead");
    }
    synchronized(canonicalDbPath.intern()) {
      PaymentEntity lastEntity = getLastEntityFromStorage();
      long newId = generateNewId(lastEntity);

      try (FileWriter writer = new FileWriter(canonicalDbPath, true)) {
        entity.setId(newId);
        writeEntityInternallyByWriter(entity, writer);
      } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
        logger.error("Cannot save entity into file due to: ", ex);
        throw new CannotParseFileException("Cannot write into file " + canonicalDbPath + ex.getMessage());
      }

      return entity;
    }
  }

  @Override
  public PaymentEntity update(PaymentEntity entity) {
    synchronized(canonicalDbPath.intern()) {
      Tuple2<String, Integer> lineWithPayment = getLineStartsWithId(entity.getId());

      if (lineWithPayment._1 == null) {
        throw new ResourceCannotBeFoundException("Resource with ID: " + entity.getId() + " cannot be found in order to update");
      }

      try {
        StringWriter writer = new StringWriter();
        writeEntityInternallyByWriter(entity, writer);
        replaceLine(lineWithPayment._2, writer.toString());
        return entity;
      } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
        logger.error("Cannot write into file", ex);
        throw new CannotParseFileException("Cannot write into file " + canonicalDbPath + ex.getMessage());
      }
    }
  }

  @Override
  public PaymentEntity deleteById(Long id) {
    synchronized(canonicalDbPath.intern()) {
      Tuple2<String, Integer> lineWithPayment = getLineStartsWithId(id);

      if (lineWithPayment._1 == null) {
        throw new ResourceCannotBeFoundException("Resource with ID: " + id + " cannot be found in order to delete");
      }
      try {
        removeLine(lineWithPayment._2);
        return mapLineIntoEntity(lineWithPayment._1);
      } catch (IOException ex) {
        logger.error("Cannot write into file", ex);
        throw new CannotParseFileException("Cannot write into file " + canonicalDbPath + ex.getMessage());
      }
    }
  }

  private void removeLine(int lineNumber) throws IOException {
    Path path = Paths.get(canonicalDbPath);
    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
    lines.remove(lineNumber);
    Files.write(path, lines, StandardCharsets.UTF_8);
  }

  public void replaceLine(int lineNumber, String data) throws IOException {
    Path path = Paths.get(canonicalDbPath);
    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
    lines.set(lineNumber, data.replaceAll("\n", ""));
    Files.write(path, lines, StandardCharsets.UTF_8);
  }

  private Tuple2<String, Integer> getLineStartsWithId(Long id) {
    String line;
    int counter = 0;
    try (FileReader fileIn = new FileReader(canonicalDbPath);
        BufferedReader reader = new BufferedReader(fileIn)) {

      while((line = reader.readLine()) != null) {
        if((line.startsWith(id +","))) {
          break;
        }
        counter++;
      }
    } catch (IOException ex) {
      logger.error("Cannot parse " + canonicalDbPath + " in order to get the line that starts with id: " + id, ex);
      throw new CannotParseFileException("Cannot read from file " + canonicalDbPath + ex.getMessage());
    }
    return new Tuple2<>(line, counter);
  }

  private void writeEntityInternallyByWriter(PaymentEntity createdEntity, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
    ColumnPositionMappingStrategy<PaymentEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
    mappingStrategy.setType(PaymentEntity.class);

    StatefulBeanToCsvBuilder<PaymentEntity> builder = new StatefulBeanToCsvBuilder<>(writer);
    StatefulBeanToCsv<PaymentEntity> beanWriter = builder.withMappingStrategy(mappingStrategy).withApplyQuotesToAll(false).build();
    beanWriter.write(createdEntity);
  }

  private long generateNewId(PaymentEntity lastEntity) {
    return lastEntity != null ? lastEntity.getId() + 1 : 1;
  }

  private PaymentEntity getLastEntityFromStorage() {
    PaymentEntity lastEntity = null;
    try (ReversedLinesFileReader fileReader = new ReversedLinesFileReader(new File(canonicalDbPath), StandardCharsets.UTF_8)) {
      String lastLine = fileReader.readLine();
      fileReader.close();

      if (lastLine != null) {
        lastEntity = mapLineIntoEntity(lastLine);
      }
    } catch (IOException ex) {
      throw new CannotParseFileException(ex, "Cannot parse " + canonicalDbPath + " File in order to get last line");
    }
    return lastEntity;
  }

  private PaymentEntity mapLineIntoEntity(String lastRecord) {
    PaymentEntity lastEntity;
    InputStream inputStream = new ByteArrayInputStream(lastRecord.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(inputStream);

    lastEntity = new CsvToBeanBuilder<PaymentEntity>(reader)
        .withType(PaymentEntity.class)
        .build()
        .parse()
        .get(0);
    return lastEntity;
  }

}
