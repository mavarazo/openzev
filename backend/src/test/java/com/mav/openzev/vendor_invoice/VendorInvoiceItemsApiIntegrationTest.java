package com.mav.openzev.vendor_invoice;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.AbstractApiIntegrationTest;
import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.VendorInvoiceItemDto;
import com.mav.openzev.vendor_invoice.entity.VendorInvoice;
import com.mav.openzev.vendor_invoice.entity.VendorInvoiceItem;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class VendorInvoiceItemsApiIntegrationTest extends AbstractApiIntegrationTest {

  private static final String V_1_VENDOR_INVOICES_ID_ITEMS =
      "/v1/vendor-invoices/{vendorInvoiceId}/items";
  private static final String V_1_VENDOR_INVOICES_ID_ITEMS_ID =
      "/v1/vendor-invoices/{vendorInvoiceId}/items/{vendorInvoiceItemId}";

  @Autowired private VendorInvoiceTestDataService vendorInvoiceTestDataService;

  @Nested
  class GetVendorInvoiceItemsTests {

    @Test
    void status200() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();
      vendorInvoiceTestDataService.newVendorInvoiceItem(i -> i.vendorInvoice(vendorInvoice));

      // act
      final ResponseEntity<VendorInvoiceItemDto[]> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID_ITEMS,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              VendorInvoiceItemDto[].class,
              vendorInvoice.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class CreateVendorInvoiceItemTests {

    @Test
    void status201() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();
      final VendorInvoiceItemDto vendorInvoiceItemDto =
          new VendorInvoiceItemDto()
              .type(VendorInvoiceItemDto.TypeEnum.CHARGE)
              .position(10)
              .name("Einheitstarif Energie")
              .quantity(15000f)
              .unit("kWh")
              .price(1f)
              .total(15000f);

      // act
      final ResponseEntity<VendorInvoiceItemDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID_ITEMS,
              HttpMethod.POST,
              new HttpEntity<>(vendorInvoiceItemDto, null),
              VendorInvoiceItemDto.class,
              vendorInvoice.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(VendorInvoiceItemDto.TypeEnum.CHARGE, VendorInvoiceItemDto::getType)
                      .returns(10, VendorInvoiceItemDto::getPosition)
                      .returns("Einheitstarif Energie", VendorInvoiceItemDto::getName)
                      .returns(null, VendorInvoiceItemDto::getDescription)
                      .returns(15000f, VendorInvoiceItemDto::getQuantity)
                      .returns("kWh", VendorInvoiceItemDto::getUnit)
                      .returns(1f, VendorInvoiceItemDto::getPrice)
                      .returns(15000f, VendorInvoiceItemDto::getTotal));
    }

    @Test
    void status400() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();
      final VendorInvoiceItemDto vendorInvoiceItemDto =
          new VendorInvoiceItemDto()
              .type(VendorInvoiceItemDto.TypeEnum.CHARGE)
              .position(10)
              .name("Einheitstarif Energie")
              .quantity(15000f)
              .unit("kWh")
              .price(1f)
              .total(10000f);

      // act
      final ResponseEntity<ErrorDto[]> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID_ITEMS,
              HttpMethod.POST,
              new HttpEntity<>(vendorInvoiceItemDto, null),
              ErrorDto[].class,
              vendorInvoice.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .singleElement()
                      .returns("vendor-invoice-item.total.invalid", ErrorDto::getCode));
    }

    @Test
    void status404() {
      // arrange
      final VendorInvoiceItemDto vendorInvoiceItemDto =
          new VendorInvoiceItemDto()
              .type(VendorInvoiceItemDto.TypeEnum.CHARGE)
              .position(10)
              .name("Einheitstarif Energie")
              .quantity(15000f)
              .unit("kWh")
              .price(1f)
              .total(15000f);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID_ITEMS,
              HttpMethod.POST,
              new HttpEntity<>(vendorInvoiceItemDto, null),
              ErrorDto.class,
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class ChangeVendorInvoiceItemTests {

    @Test
    void status200() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();
      final VendorInvoiceItem vendorInvoiceItem =
          vendorInvoiceTestDataService.newVendorInvoiceItem(i -> i.vendorInvoice(vendorInvoice));

      final VendorInvoiceItemDto vendorInvoiceItemDto =
          new VendorInvoiceItemDto()
              .type(VendorInvoiceItemDto.TypeEnum.CHARGE)
              .position(10)
              .name("Einheitstarif Energie")
              .quantity(15000f)
              .unit("kWh")
              .price(1f)
              .total(15000f);

      // act
      final ResponseEntity<VendorInvoiceItemDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID_ITEMS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(vendorInvoiceItemDto, null),
              VendorInvoiceItemDto.class,
              vendorInvoice.getId(),
              vendorInvoiceItem.getId());

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(
              r ->
                  assertThat(r.getBody())
                      .returns(VendorInvoiceItemDto.TypeEnum.CHARGE, VendorInvoiceItemDto::getType)
                      .returns(10, VendorInvoiceItemDto::getPosition)
                      .returns("Einheitstarif Energie", VendorInvoiceItemDto::getName)
                      .returns(null, VendorInvoiceItemDto::getDescription)
                      .returns(15000f, VendorInvoiceItemDto::getQuantity)
                      .returns("kWh", VendorInvoiceItemDto::getUnit)
                      .returns(1f, VendorInvoiceItemDto::getPrice)
                      .returns(15000f, VendorInvoiceItemDto::getTotal));
    }

    @Test
    void status404() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();

      final VendorInvoiceItemDto vendorInvoiceItemDto =
          new VendorInvoiceItemDto()
              .type(VendorInvoiceItemDto.TypeEnum.CHARGE)
              .position(10)
              .name("Einheitstarif Energie")
              .quantity(15000f)
              .unit("kWh")
              .price(1f)
              .total(15000f);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID_ITEMS_ID,
              HttpMethod.PUT,
              new HttpEntity<>(vendorInvoiceItemDto, null),
              ErrorDto.class,
              vendorInvoice.getId(),
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class DeleteVendorInvoiceItemTests {

    @Test
    void status200() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();
      final VendorInvoiceItem vendorInvoiceItem =
          vendorInvoiceTestDataService.newVendorInvoiceItem(i -> i.vendorInvoice(vendorInvoice));

      // act
      final ResponseEntity<Void> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID_ITEMS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              Void.class,
              vendorInvoice.getId(),
              vendorInvoiceItem.getId());

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final VendorInvoice vendorInvoice = vendorInvoiceTestDataService.newVendorInvoice();

      // act
      final ResponseEntity<Void> response =
          restTemplate.exchange(
              V_1_VENDOR_INVOICES_ID_ITEMS_ID,
              HttpMethod.DELETE,
              HttpEntity.EMPTY,
              Void.class,
              vendorInvoice.getId(),
              UUID.randomUUID());

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }
}
