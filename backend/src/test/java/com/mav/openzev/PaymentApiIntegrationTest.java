package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.*;
import com.mav.openzev.api.model.InvoiceStatus;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class PaymentApiIntegrationTest extends AbstractApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @Nested
  class GetPaymentsTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_payments(InvoiceModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      testDatabaseService.insert(PaymentModels.getPayment(invoice));

      // act
      final ResponseEntity<PaymentDto[]> response =
          restTemplate.exchange(
              UriFactory.invoices_payments(InvoiceModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              PaymentDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class GetPaymentTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.payments(PaymentModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("payment_not_found", ErrorDto::getCode)
          .returns(
              "payment with id '437b6478-a39f-41ce-a0b7-490458aeca5b' not found",
              ErrorDto::getMessage);
    }

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      testDatabaseService.insert(PaymentModels.getPayment(invoice));

      // act
      final ResponseEntity<PaymentDto> response =
          restTemplate.exchange(
              UriFactory.payments(PaymentModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              PaymentDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class CreatePaymentTests {

    @ParameterizedTest
    @RequiredSource(ModifiablePaymentDto.class)
    void status400(final ModifiablePaymentDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_payments(InvoiceModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404_invoice_not_found() {
      // arrange
      final ModifiablePaymentDto requestBody =
          new ModifiablePaymentDto()
              .amount(BigDecimal.valueOf(2))
              .received(LocalDate.of(2024, 1, 31));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_payments(InvoiceModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      final ModifiablePaymentDto requestBody =
          new ModifiablePaymentDto().amount(Constants.TWO).received(Constants._2024_01_31);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.invoices_payments(InvoiceModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.payments(response.getBody()),
                  HttpMethod.GET,
                  new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
                  PaymentDto.class)
              .getBody());

      assertThat(
              restTemplate
                  .exchange(
                      UriFactory.invoices(InvoiceModels.UUID),
                      HttpMethod.GET,
                      new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
                      InvoiceDto.class)
                  .getBody())
          .returns(InvoiceStatus.PAID, InvoiceDto::getStatus);
    }
  }

  @Nested
  class ChangePaymentTests {

    @ParameterizedTest
    @RequiredSource(ModifiablePaymentDto.class)
    void status400(final ModifiablePaymentDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.payments(PaymentModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiablePaymentDto requestBody =
          new ModifiablePaymentDto().amount(Constants.TWO).received(Constants._2024_01_31);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.payments(PaymentModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      testDatabaseService.insert(PaymentModels.getPayment(invoice));

      final ModifiablePaymentDto requestBody =
          new ModifiablePaymentDto().amount(Constants.TWO).received(Constants._2024_01_31);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.payments(PaymentModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.payments(response.getBody()),
                  HttpMethod.GET,
                  new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
                  PaymentDto.class)
              .getBody());
    }
  }

  @Nested
  class DeletePaymentTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.items(ItemModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status204() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      testDatabaseService.insert(PaymentModels.getPayment(invoice));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.payments(PaymentModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
