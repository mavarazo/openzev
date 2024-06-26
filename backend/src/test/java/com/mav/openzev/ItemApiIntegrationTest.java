package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ItemDto;
import com.mav.openzev.api.model.ModifiableItemDto;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.*;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.repository.ItemRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class ItemApiIntegrationTest extends AbstractApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @Autowired private ItemRepository itemRepository;
  @Autowired private InvoiceRepository invoiceRepository;

  @Nested
  class GetItemsTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      final Product product = testDatabaseService.insert(ProductModels.getProduct());
      testDatabaseService.insert(ItemModels.getItem(invoice, product));

      // act
      final ResponseEntity<ItemDto[]> response =
          restTemplate.exchange(
              UriFactory.invoices_items(InvoiceModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ItemDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .satisfies(r -> assertThat(r.getBody()).hasSize(1));
    }
  }

  @Nested
  class GetInvoiceTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.items(ItemModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("item_not_found", ErrorDto::getCode)
          .returns(
              "item with id '4db55aea-203b-4d19-a8c7-abaa8cff39e8' not found",
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
      final Product product = testDatabaseService.insert(ProductModels.getProduct());
      testDatabaseService.insert(ItemModels.getItem(invoice, product));

      // act
      final ResponseEntity<ItemDto> response =
          restTemplate.exchange(
              UriFactory.items(ItemModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ItemDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, ResponseEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class CreateItemTests {

    @ParameterizedTest
    @RequiredSource(ModifiableItemDto.class)
    void status400(final ModifiableItemDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_items(InvoiceModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404_invoice_not_found() {
      // arrange
      final ModifiableItemDto requestBody =
          new ModifiableItemDto()
              .productId(ProductModels.UUID)
              .quantity(Constants.ONE)
              .price(Constants.TWO)
              .amount(Constants.TWO);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_items(InvoiceModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status404_product_not_found() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      testDatabaseService.insert(
          InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());

      final ModifiableItemDto requestBody =
          new ModifiableItemDto()
              .productId(ProductModels.UUID)
              .quantity(Constants.ONE)
              .price(Constants.TWO)
              .amount(Constants.TWO);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.invoices_items(InvoiceModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
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
      testDatabaseService.insert(ProductModels.getProduct());

      final ModifiableItemDto requestBody =
          new ModifiableItemDto()
              .productId(ProductModels.UUID)
              .quantity(Constants.ONE)
              .price(Constants.TWO)
              .amount(Constants.TWO);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.invoices_items(InvoiceModels.UUID),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(invoiceRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              invoice ->
                  assertThat(invoice.getItems())
                      .hasSize(1)
                      .singleElement()
                      .returns(ProductModels.UUID, i -> i.getProduct().getUuid())
                      .returns(Constants.ONE, Item::getQuantity)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(Constants.TWO, Item::getPrice));
    }
  }

  @Nested
  class ChangeItemTests {

    @Test
    void status200() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      final Product product = testDatabaseService.insert(ProductModels.getProduct());
      testDatabaseService.insert(ItemModels.getItem(invoice, product));

      final ModifiableItemDto requestBody =
          new ModifiableItemDto()
              .productId(ProductModels.UUID)
              .quantity(Constants.ONE)
              .price(Constants.TWO)
              .amount(Constants.TWO);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.items(ItemModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      assertThat(itemRepository.findByUuid(response.getBody()))
          .isPresent()
          .hasValueSatisfying(
              item ->
                  assertThat(item)
                      .returns(ProductModels.UUID, i -> i.getProduct().getUuid())
                      .returns(Constants.ONE, Item::getQuantity)
                      .usingComparatorForType(
                          BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .returns(Constants.TWO, Item::getPrice));
    }

    @ParameterizedTest
    @RequiredSource(ModifiableItemDto.class)
    void status400(final ModifiableItemDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.items(InvoiceModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiableItemDto requestBody =
          new ModifiableItemDto()
              .productId(ProductModels.UUID)
              .quantity(Constants.ONE)
              .price(Constants.TWO)
              .amount(Constants.TWO);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.items(ItemModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status404_product_not_found() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      final Product product = testDatabaseService.insert(ProductModels.getProduct());
      testDatabaseService.insert(ItemModels.getItem(invoice, product));

      final ModifiableItemDto requestBody =
          new ModifiableItemDto()
              .productId(UUID.randomUUID())
              .quantity(Constants.ONE)
              .price(Constants.TWO)
              .amount(Constants.TWO);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.items(InvoiceModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }
  }

  @Nested
  class DeleteItemTests {

    @Test
    void status204() {
      // arrange
      final Unit unit = testDatabaseService.insert(UnitModels.getUnit());
      final Owner recipient = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().unit(unit).recipient(recipient).build());
      final Product product = testDatabaseService.insert(ProductModels.getProduct());
      testDatabaseService.insert(ItemModels.getItem(invoice, product));

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.items(ItemModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }

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
  }
}
