package com.krysz.paymentshandler.adapters;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.krysz.paymentshandler.domain.ports.PaymentRepository;
import com.krysz.paymentshandler.domain.ports.model.PaymentEntity;

import static org.junit.jupiter.api.Assertions.*;

class CsvRepositoryTest {

  public static final URL TEST_DB = CsvRepositoryTest.class.getClassLoader().getResource("testDb.csv");
  public static final String TEST_DB_COPY_PATH ="testDb-temp.csv";
  private static PaymentRepository SUT;

  @BeforeAll
  public static void setUp() throws URISyntaxException, IOException {
    File originalDb = new File(Objects.requireNonNull(TEST_DB).toURI());
    File copy = new File(TEST_DB_COPY_PATH);
    copy.deleteOnExit();

    FileUtils.copyFile(originalDb, copy);
    SUT = new CsvRepository(TEST_DB_COPY_PATH);
  }


  @Test
  public void shouldReturn4ElementsForFindAll() {
    List<PaymentEntity> result = SUT.findAll();
    assertEquals(4, result.size());
  }

  @Test
  public void shouldReturnElementWithId5() {
    PaymentEntity result = SUT.findById(5L);
    assertEquals(5L, result.getId());
  }

  @Test
  public void shouldReturnIfElemIsNotInDb() {
    PaymentEntity result = SUT.findById(1L);
    assertNull(result);
  }

  @Test
  public void shouldSaveNewElemIntoDb() {
    PaymentEntity entity = new PaymentEntity(null, 2L, "UK1237", "USD", BigDecimal.ONE);
    PaymentEntity savedEntity = SUT.save(entity);

    PaymentEntity result = SUT.findById(savedEntity.getId());

    assertEquality(savedEntity, result);
  }

  @Test
  public void shouldUpdateElemInDb() {
    PaymentEntity entity = new PaymentEntity(3L, 2L, "UK1237", "USD", BigDecimal.ONE);
    SUT.update(entity);

    PaymentEntity result = SUT.findById(3L);

    assertEquality(entity, result);
  }

  @Test
  public void shouldDeleteElemInDb() {
    PaymentEntity deleted = SUT.deleteById(3L);

    PaymentEntity found = SUT.findById(3L);

    assertNotNull(deleted);
    assertNull(found);
  }

  @Test
  public void shouldThrowUnsupportedOpExceptionWhenTryToSaveElementWithFilledId() {
    PaymentEntity entity = new PaymentEntity(2L, 2L, "UK1237", "USD", BigDecimal.ONE);
    UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> SUT.save(entity));

    assertEquals("Cannot save the entity with set ID! Use update instead", exception.getMessage());
  }

  private void assertEquality(PaymentEntity entity, PaymentEntity result) {
    assertEquals(entity.getId(), result.getId());
    assertEquals(entity.getUser(), result.getUser());
    assertEquals(entity.getAmount(), result.getAmount());
    assertEquals(entity.getCurrency(), result.getCurrency());
    assertEquals(entity.getBankAccount(), result.getBankAccount());
  }
}
