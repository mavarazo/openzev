package com.mav.openzev.vendor_invoice;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.AbstractApiIntegrationTest;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.VendorInvoiceDto;
import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class VendorInvoicesApiIntegrationTests extends AbstractApiIntegrationTest {

  private static final String V_1_VENDOR_INVOICES = "/v1/vendor-invoices";
  private static final String V_1_VENDOR_INVOICES_ID = "/v1/vendor-invoices/{vendorInvoiceId}";

  @Autowired private VendorInvoiceTestDataService vendorInvoiceTestDataService;

  @Nested
  class GetVendorInvoicesTests {

    @Test
    void status200() {
      // arrange
      vendorInvoiceTestDataService.newVendorInvoice();

      // act
      final ResponseEntity<VendorInvoiceDto[]> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES, HttpMethod.GET, HttpEntity.EMPTY, VendorInvoiceDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class CreateVendorInvoiceTests {

    @Test
    void status201() {
      // arrange
      final VendorInvoiceDto vendorInvoiceDto =
          new VendorInvoiceDto()
              .date(LocalDate.of(2025, 4, 30))
              .periodFrom(LocalDate.of(2025, 1, 1))
              .periodUpto(LocalDate.of(2025, 3, 31))
              .dueDate(LocalDate.of(2025, 5, 30));

      // act
      final ResponseEntity<VendorInvoiceDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES,
              HttpMethod.POST,
              new HttpEntity<>(vendorInvoiceDto, null),
              VendorInvoiceDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(VendorInvoiceDto.StateEnum.OPEN, VendorInvoiceDto::getState)
                      .returns(LocalDate.of(2025, 4, 30), VendorInvoiceDto::getDate)
                      .returns(LocalDate.of(2025, 1, 1), VendorInvoiceDto::getPeriodFrom)
                      .returns(LocalDate.of(2025, 3, 31), VendorInvoiceDto::getPeriodUpto)
                      .returns(LocalDate.of(2025, 5, 30), VendorInvoiceDto::getDueDate));
    }
  }

  @Nested
  class GetVendorInvoiceTests {

    @Test
    void status200() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();

      // act
      final ResponseEntity<VendorInvoiceDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              VendorInvoiceDto.class,
              vendorInvoice.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(VendorInvoiceDto.StateEnum.OPEN, VendorInvoiceDto::getState)
                      .returns(LocalDate.of(2025, 4, 30), VendorInvoiceDto::getDate)
                      .returns(LocalDate.of(2025, 1, 1), VendorInvoiceDto::getPeriodFrom)
                      .returns(LocalDate.of(2025, 3, 31), VendorInvoiceDto::getPeriodUpto)
                      .returns(LocalDate.of(2025, 5, 30), VendorInvoiceDto::getDueDate));
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class ChangeVendorInvoiceTests {

    @Test
    void status200() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();
      final VendorInvoiceDto vendorInvoiceDto =
          new VendorInvoiceDto()
              .date(LocalDate.of(2025, 4, 30))
              .periodFrom(LocalDate.of(2025, 4, 1))
              .periodUpto(LocalDate.of(2025, 6, 30))
              .dueDate(LocalDate.of(2025, 5, 30));

      // act
      final ResponseEntity<VendorInvoiceDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID,
              HttpMethod.PUT,
              new HttpEntity<>(vendorInvoiceDto, null),
              VendorInvoiceDto.class,
              vendorInvoice.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(VendorInvoiceDto.StateEnum.OPEN, VendorInvoiceDto::getState)
                      .returns(LocalDate.of(2025, 4, 30), VendorInvoiceDto::getDate)
                      .returns(LocalDate.of(2025, 4, 1), VendorInvoiceDto::getPeriodFrom)
                      .returns(LocalDate.of(2025, 6, 30), VendorInvoiceDto::getPeriodUpto)
                      .returns(LocalDate.of(2025, 5, 30), VendorInvoiceDto::getDueDate));
    }

    @Test
    void status404() {
      // arrange
      final VendorInvoiceDto vendorInvoiceDto =
          new VendorInvoiceDto()
              .periodFrom(LocalDate.of(2025, 4, 1))
              .periodUpto(LocalDate.of(2025, 6, 30));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID,
              HttpMethod.PUT,
              new HttpEntity<>(vendorInvoiceDto, null),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class DeleteVendorInvoiceTests {

    @Test
    void status200() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();

      // act
      final ResponseEntity<Void> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              Void.class,
              vendorInvoice.getId());

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<VendorInvoiceDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              VendorInvoiceDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }
}
