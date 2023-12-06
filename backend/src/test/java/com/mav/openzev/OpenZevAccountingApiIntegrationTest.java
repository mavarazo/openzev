package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.AccountingDto;
import com.mav.openzev.api.model.DocumentDto;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableAccountingDto;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.AccountingModels;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.model.AgreementModels;
import com.mav.openzev.model.Document;
import com.mav.openzev.model.DocumentModels;
import com.mav.openzev.model.InvoiceModels;
import com.mav.openzev.model.Unit;
import com.mav.openzev.model.UnitModels;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.DocumentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.SneakyThrows;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenZevAccountingApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private com.mav.openzev.TestDatabaseService testDatabaseService;

  @Autowired private AccountingRepository accountingRepository;
  @Autowired private DocumentRepository documentRepository;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetAccountingsTests {

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(AccountingModels.getAccounting());

      // act
      final ResponseEntity<AccountingDto[]> response =
          restTemplate.exchange(
              UriFactory.accountings(), HttpMethod.GET, HttpEntity.EMPTY, AccountingDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetAccountingTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings(AccountingModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_not_found", ErrorDto::getCode)
          .returns(
              "accounting with id '27bc46ee-4d28-492b-a849-e52dbc5ded1a' not found",
              ErrorDto::getMessage);
    }

    @Test
    void status200() {
      // arrange
        final Agreement agreement = testDatabaseService.insert(AgreementModels.getAgreement());
        testDatabaseService.insert(
          AccountingModels.getAccounting().toBuilder()
              .agreement(agreement)
              .build());

      // act
      final ResponseEntity<AccountingDto> response =
          restTemplate.exchange(
              UriFactory.accountings(AccountingModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              AccountingDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(AccountingModels.UUID, AccountingDto::getId)
                      .returns(AgreementModels.UUID, AccountingDto::getAgreementId)
                      .returns(AccountingModels._2024_01_01, AccountingDto::getPeriodFrom)
                      .returns(AccountingModels._2024_12_31, AccountingDto::getPeriodUpto)
                      .returns(
                          AccountingModels.ABRECHNUNG_2024_ZAEHLER_1, AccountingDto::getSubject)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(AccountingModels._1000, AccountingDto::getAmountHighTariff)
                      .returns(AccountingModels._500, AccountingDto::getAmountLowTariff)
                      .returns(AccountingModels._1500, AccountingDto::getAmountTotal));
    }
  }

  @Nested
  class CreateAccountingTests {

    @Test
    void status201() {
      // arrange
      testDatabaseService.insert(AgreementModels.getAgreement());

      // arrange
      final ModifiableAccountingDto requestBody =
          new ModifiableAccountingDto()
              .agreementId(AgreementModels.UUID)
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31))
              .subject("Abrechnung 2023")
              .amountHighTariff(BigDecimal.valueOf(100))
              .amountLowTariff(BigDecimal.valueOf(75))
              .amountTotal(BigDecimal.valueOf(175));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.accountings(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(accountingRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              accounting ->
                  assertThat(accounting)
                      .returns(AgreementModels.UUID, a -> a.getAgreement().getUuid())
                      .returns(LocalDate.of(2023, 1, 1), Accounting::getPeriodFrom)
                      .returns(LocalDate.of(2023, 12, 31), Accounting::getPeriodUpto)
                      .returns("Abrechnung 2023", Accounting::getSubject)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(100), Accounting::getAmountHighTariff)
                      .returns(BigDecimal.valueOf(75), Accounting::getAmountLowTariff)
                      .returns(BigDecimal.valueOf(175), Accounting::getAmountTotal));
    }
  }

  @Nested
  class ChangeAccountingTests {

    @Test
    void status404() {
      // arrange
      final ModifiableAccountingDto requestBody =
          new ModifiableAccountingDto()
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 12, 31))
              .subject("Abrechnung 2023")
              .amountHighTariff(BigDecimal.valueOf(100))
              .amountLowTariff(BigDecimal.valueOf(75))
              .amountTotal(BigDecimal.valueOf(175));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings(AccountingModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_not_found", ErrorDto::getCode)
          .returns(
              "accounting with id '27bc46ee-4d28-492b-a849-e52dbc5ded1a' not found",
              ErrorDto::getMessage);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(AccountingModels.getAccounting());

      final ModifiableAccountingDto requestBody =
          new ModifiableAccountingDto()
              .periodFrom(LocalDate.of(2023, 1, 1))
              .periodUpto(LocalDate.of(2023, 3, 31))
              .subject("Abrechnung 01/2023")
              .amountHighTariff(BigDecimal.valueOf(100))
              .amountLowTariff(BigDecimal.valueOf(75))
              .amountTotal(BigDecimal.valueOf(175));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.accountings(AccountingModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(accountingRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              accounting ->
                  assertThat(accounting)
                      .returns(null, Accounting::getAgreement)
                      .returns(LocalDate.of(2023, 1, 1), Accounting::getPeriodFrom)
                      .returns(LocalDate.of(2023, 3, 31), Accounting::getPeriodUpto)
                      .returns("Abrechnung 01/2023", Accounting::getSubject)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(BigDecimal.valueOf(100), Accounting::getAmountHighTariff)
                      .returns(BigDecimal.valueOf(75), Accounting::getAmountLowTariff)
                      .returns(BigDecimal.valueOf(175), Accounting::getAmountTotal));
    }
  }

  @Nested
  class DeleteAccountingTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings(AccountingModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_not_found", ErrorDto::getCode);
    }

    @Test
    void status422() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      testDatabaseService.insert(
          AccountingModels.getAccounting()
              .addInvoice(InvoiceModels.getInvoice().toBuilder().unit(unit).build()));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings(AccountingModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_has_invoice", ErrorDto::getCode);
    }

    @Test
    void status204() {
      // arrange
      testDatabaseService.insert(AccountingModels.getAccounting());

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.accountings(AccountingModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class GetDocumentTests {

    @Test
    void status404_accounting() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings_documents(AccountingModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_not_found", ErrorDto::getCode);
    }

    @Test
    @SneakyThrows
    void status200() {
      // arrange
      final Accounting accounting = testDatabaseService.insert(AccountingModels.getAccounting());
      testDatabaseService.merge(accounting.addDocument(DocumentModels.getDocument()));

      // act
      final ResponseEntity<DocumentDto[]> response =
          restTemplate.exchange(
              UriFactory.accountings_documents(AccountingModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, null),
              DocumentDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class CreateDocumentTests {

    @Test
    void status400_multipart_required() {
      // arrange
      final HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      final MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings_documents(AccountingModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, headers),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("multipart_file_required", ErrorDto::getCode);
    }

    @Test
    void status404() {
      // arrange
      final HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      final MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
      requestBody.add("content", new ClassPathResource("pdf.test-data/dummy.pdf"));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.accountings_documents(AccountingModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, headers),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("accounting_not_found", ErrorDto::getCode);
    }

    @Test
    void status200() {
      testDatabaseService.insert(AccountingModels.getAccounting());

      // arrange
      final HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      final MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
      requestBody.add("content", new ClassPathResource("pdf.test-data/dummy.pdf"));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.accountings_documents(AccountingModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, headers),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(documentRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              document ->
                  assertThat(document)
                      .isNotNull()
                      .doesNotReturn(null, Document::getRefId)
                      .returns(Accounting.class.getSimpleName(), Document::getRefType)
                      .returns("content", Document::getName)
                      .returns("dummy.pdf", Document::getFilename)
                      .returns(MediaType.APPLICATION_PDF_VALUE, Document::getMediaType)
                      .doesNotReturn(null, Document::getData)
                      .doesNotReturn(null, Document::getThumbnail));
    }
  }
}
