package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiablePaymentDto;
import com.mav.openzev.api.model.PaymentDto;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.Constants;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.InvoiceModels;
import com.mav.openzev.model.ItemModels;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.OwnerModels;
import com.mav.openzev.model.PaymentModels;
import com.mav.openzev.model.Unit;
import com.mav.openzev.model.UnitModels;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@MockBeans({@MockBean(JavaMailSenderImpl.class)})
public class OpenZevPaymentApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetPaymentsTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_payments(InvoiceModels.UUID),
              HttpMethod.GET,
              HttpEntity.EMPTY,
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
              HttpEntity.EMPTY,
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
              HttpEntity.EMPTY,
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
              HttpEntity.EMPTY,
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
              new HttpEntity<>(requestBody, null),
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
              new HttpEntity<>(requestBody, null),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    @Transactional
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
              new HttpEntity<>(requestBody, null),
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
                  HttpEntity.EMPTY,
                  PaymentDto.class)
              .getBody());
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
              new HttpEntity<>(requestBody, null),
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
              new HttpEntity<>(requestBody, null),
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
              new HttpEntity<>(requestBody, null),
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
                  HttpEntity.EMPTY,
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
              UriFactory.items(ItemModels.UUID), HttpMethod.DELETE, null, ErrorDto.class);

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
              UriFactory.payments(PaymentModels.UUID), HttpMethod.DELETE, null, UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
