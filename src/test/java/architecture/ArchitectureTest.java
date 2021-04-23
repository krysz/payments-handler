package architecture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static org.assertj.core.api.Assertions.assertThat;

@AnalyzeClasses(importOptions = { ImportOption.DoNotIncludeJars.class,
    ImportOption.DoNotIncludeArchives.class,
    ImportOption.DoNotIncludeTests.class })
public class ArchitectureTest {

  private static final String DOMAIN_MODULE_PACKAGE = "com.krysz.paymentshandler.domain";
  private static final String API_MODULE_PACKAGE = "com.krysz.paymentshandler.api";

  @ArchTest
  private final ArchRule no_generic_exceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

  @ArchTest
  static ArchRule all_public_methods_in_the_REST_controller_should_return_response_entity =
      methods()
          .that()
          .areDeclaredInClassesThat()
          .areAnnotatedWith(RestController.class)
          .and()
          .arePublic()
          .should()
          .haveRawReturnType(ResponseEntity.class)
          .because("We want to explicitly control HTTP response code and body");

  @ArchTest
  static ArchRule configuration_classes_end_with_Configuration =
      classes()
          .that()
          .areAnnotatedWith(Configuration.class)
          .should()
          .haveNameMatching(".*Configuration");

  @ArchTest
  static ArchRule configuration_classes_should_be_annotated_with_Configuration =
      classes()
          .that()
          .haveNameMatching(".*Configuration")
          .should()
          .beAnnotatedWith(Configuration.class);

  @ArchTest
  static ArchRule fields_should_not_be_annotated_with_autowired =
      fields()
          .should()
          .notBeAnnotatedWith(Autowired.class)
          .because("We prefer constructor injection together with Lombok @AllArgsConstructor");

  @ArchTest
  public static final ArchRule domain_should_not_depend_on_api =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN_MODULE_PACKAGE)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(API_MODULE_PACKAGE);

  @ArchTest
  public static void verifyExpectedPackageStructure(JavaClasses importedClasses) {
    assertThat(importedClasses.containPackage(DOMAIN_MODULE_PACKAGE))
        .as("Project structure has changed. Package %s not found.", DOMAIN_MODULE_PACKAGE)
        .isTrue();
    assertThat(importedClasses.containPackage(API_MODULE_PACKAGE))
        .as("Project structure has changed. Package %s not found.", API_MODULE_PACKAGE)
        .isTrue();
  }
}
