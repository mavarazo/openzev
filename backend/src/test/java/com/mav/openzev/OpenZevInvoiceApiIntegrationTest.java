package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.InvoiceDirection;
import com.mav.openzev.api.model.InvoiceDto;
import com.mav.openzev.api.model.ModifiableInvoiceDto;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.MimeMessageAssert;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.*;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// @MockBeans({@MockBean(JavaMailSenderImpl.class)})
public class OpenZevInvoiceApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @MockBean(JavaMailSenderImpl.class)
  private JavaMailSenderImpl javaMailSender;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetInvoicesTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      // act
      final ResponseEntity<InvoiceDto[]> response =
          restTemplate.exchange(
              UriFactory.invoices(), HttpMethod.GET, HttpEntity.EMPTY, InvoiceDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, ResponseEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class GetInvoiceTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices(InvoiceModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_not_found", ErrorDto::getCode)
          .returns(
              "invoice with id '414d2033-3b17-4e68-b69e-e483db0dc90b' not found",
              ErrorDto::getMessage);
    }

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      // act
      final ResponseEntity<InvoiceDto> response =
          restTemplate.exchange(
              UriFactory.invoices(InvoiceModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              InvoiceDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, ResponseEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class CreateInvoiceTests {

    @ParameterizedTest
    @RequiredSource(ModifiableInvoiceDto.class)
    void status400(final ModifiableInvoiceDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status201() {
      // arrange
      testDatabaseService.insert(UnitModels.getUnit());
      testDatabaseService.insert(OwnerModels.getOwner());

      final ModifiableInvoiceDto requestBody =
          new ModifiableInvoiceDto()
              .unitId(UnitModels.UUID)
              .recipientId(OwnerModels.UUID)
              .status(com.mav.openzev.api.model.InvoiceStatus.DRAFT)
              .direction(InvoiceDirection.OUTGOING)
              .subject("Lorem ipsum")
              .dueDate(LocalDate.of(2024, 1, 31));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.invoices(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.invoices(response.getBody()),
                  HttpMethod.GET,
                  HttpEntity.EMPTY,
                  InvoiceDto.class)
              .getBody());
    }
  }

  @Nested
  class ChangeInvoiceTests {

    @ParameterizedTest
    @RequiredSource(ModifiableInvoiceDto.class)
    void status400(final ModifiableInvoiceDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices(InvoiceModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiableInvoiceDto requestBody =
          new ModifiableInvoiceDto()
              .unitId(UnitModels.UUID)
              .recipientId(OwnerModels.UUID)
              .status(com.mav.openzev.api.model.InvoiceStatus.DRAFT)
              .direction(InvoiceDirection.OUTGOING)
              .subject("Lorem ipsum")
              .dueDate(LocalDate.of(2024, 1, 31));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices(InvoiceModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_not_found", ErrorDto::getCode)
          .returns(
              "invoice with id '414d2033-3b17-4e68-b69e-e483db0dc90b' not found",
              ErrorDto::getMessage);
    }

    @Test
    void status404_recipient_not_found() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      final ModifiableInvoiceDto requestBody =
          new ModifiableInvoiceDto()
              .unitId(UnitModels.UUID)
              .recipientId(UUID.fromString("2134b977-0d56-48ec-b85f-1c623ff934b7"))
              .status(com.mav.openzev.api.model.InvoiceStatus.DRAFT)
              .direction(InvoiceDirection.OUTGOING)
              .subject("Lorem ipsum")
              .dueDate(LocalDate.of(2024, 1, 31));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices(InvoiceModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("owner_not_found", ErrorDto::getCode)
          .returns(
              "owner with id '2134b977-0d56-48ec-b85f-1c623ff934b7' not found",
              ErrorDto::getMessage);
    }

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      final ModifiableInvoiceDto requestBody =
          new ModifiableInvoiceDto()
              .unitId(UnitModels.UUID)
              .recipientId(OwnerModels.UUID)
              .status(com.mav.openzev.api.model.InvoiceStatus.DRAFT)
              .direction(InvoiceDirection.OUTGOING)
              .subject("Lorem ipsum")
              .dueDate(LocalDate.of(2024, 1, 31));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.invoices(InvoiceModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, null),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.invoices(response.getBody()),
                  HttpMethod.GET,
                  HttpEntity.EMPTY,
                  InvoiceDto.class)
              .getBody());
    }
  }

  @Nested
  class DeleteInvoiceTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices(InvoiceModels.UUID),
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_not_found", ErrorDto::getCode);
    }

    @Test
    void status204() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.invoices(InvoiceModels.UUID),
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class GetPdfTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_pdf(InvoiceModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_not_found", ErrorDto::getCode);
    }

    @Test
    void status200() {
      testDatabaseService.insert(RepresentativeModels.getRepresentative());
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      // act
      final ResponseEntity<Resource> response =
          restTemplate.exchange(
              UriFactory.invoices_pdf(InvoiceModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              Resource.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .extracting(
              b -> {
                try {
                  return b.contentLength();
                } catch (final IOException e) {
                  return 0L;
                }
              },
              InstanceOfAssertFactories.LONG)
          .isGreaterThan(0);
    }
  }

  @Nested
  class SendEmailTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_email(InvoiceModels.UUID),
              HttpMethod.POST,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_not_found", ErrorDto::getCode);
    }

    @ParameterizedTest
    @EnumSource(
        value = com.mav.openzev.model.InvoiceStatus.class,
        mode = EnumSource.Mode.EXCLUDE,
        names = {"DRAFT"})
    void status422_invalid_state(final com.mav.openzev.model.InvoiceStatus invoiceStatus) {
      // arrange
      testDatabaseService.insert(RepresentativeModels.getRepresentative());
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder()
              .unit(unit)
              .recipient(recipient)
              .status(invoiceStatus)
              .build());

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_email(InvoiceModels.UUID),
              HttpMethod.POST,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("invoice_has_wrong_status", ErrorDto::getCode);
    }

    @Test
    void status422_recipient_has_no_email() {
      testDatabaseService.insert(RepresentativeModels.getRepresentative());
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient =
          testDatabaseService.insert(OwnerModels.getOwner().toBuilder().email(null).build());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_email(InvoiceModels.UUID),
              HttpMethod.POST,
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("recipient_of_invoice_has_no_email", ErrorDto::getCode);
    }

    @Test
    void status200() {
      testDatabaseService.insert(RepresentativeModels.getRepresentative());
      testDatabaseService.insert(BankAccountModels.getBankAccount());

      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      final MimeMessage message = new MimeMessage((Session) null);
      when(javaMailSender.createMimeMessage()).thenReturn(message);
      doNothing().when(javaMailSender).send(any(MimeMessage.class));

      // act
      final ResponseEntity<Resource> response =
          restTemplate.exchange(
              UriFactory.invoices_email(InvoiceModels.UUID),
              HttpMethod.POST,
              HttpEntity.EMPTY,
              Resource.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);

      verify(javaMailSender).send(any(MimeMessage.class));
      MimeMessageAssert.assertThat(message)
          .hasSubject(
              "Lorem ipsum"); // hasContent checks with apache commons email 2.0 as soon as released
    }
  }
}
