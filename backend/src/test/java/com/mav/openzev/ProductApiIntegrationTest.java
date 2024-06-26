package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableProductDto;
import com.mav.openzev.api.model.ProductDto;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.*;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class ProductApiIntegrationTest extends AbstractApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @Nested
  class GetProductsTests {

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(ProductModels.getProduct());

      // act
      final ResponseEntity<ProductDto[]> response =
          restTemplate.exchange(
              UriFactory.products(),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ProductDto[].class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }
  }

  @Nested
  class GetProductTests {

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(ProductModels.getProduct());

      // act
      final ResponseEntity<ProductDto> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ProductDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(response.getBody());
    }

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("product_not_found", ErrorDto::getCode)
          .returns(
              "product with id '370f9494-8f2a-4c52-9b6e-5509415d0e4a' not found",
              ErrorDto::getMessage);
    }
  }

  @Nested
  class CreateProductTests {

    @ParameterizedTest
    @RequiredSource(ModifiableProductDto.class)
    void status400(final ModifiableProductDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.products(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    @Transactional
    void status200() {
      // arrange
      final ModifiableProductDto requestBody =
          new ModifiableProductDto().subject("Lorem ipsum").price(Constants.TWO).notes("dolor");

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.products(),
              HttpMethod.POST,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.CREATED, ResponseEntity::getStatusCode)
          .doesNotReturn(null, HttpEntity::getBody);

      jsonJacksonApprovals.verifyAsJson(
          restTemplate
              .exchange(
                  UriFactory.products(response.getBody()),
                  HttpMethod.GET,
                  new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
                  ProductDto.class)
              .getBody());
    }
  }

  @Nested
  class ChangeProductTests {

    @ParameterizedTest
    @RequiredSource(ModifiableProductDto.class)
    void status400(final ModifiableProductDto requestBody) {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.BAD_REQUEST, ResponseEntity::getStatusCode);
    }

    @Test
    void status404() {
      // arrange
      final ModifiableProductDto requestBody =
          new ModifiableProductDto().subject("Lorem ipsum").price(Constants.TWO);

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID),
              HttpMethod.PUT,
              new HttpEntity<>(requestBody, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(ProductModels.getProduct());

      final ModifiableProductDto requestBody =
          new ModifiableProductDto().subject("Lorem ipsum").price(Constants.TWO);

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID),
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
                  UriFactory.products(response.getBody()),
                  HttpMethod.GET,
                  new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
                  ProductDto.class)
              .getBody());
    }
  }

  @Nested
  class DeleteProductTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status422() {
      // arrange
      final Product product = testDatabaseService.insert(ProductModels.getProduct());
      final Owner owner = testDatabaseService.insert(OwnerModels.getOwner());
      final Invoice invoice =
          testDatabaseService.insert(
              InvoiceModels.getInvoice().toBuilder().recipient(owner).unit(null).build());
      testDatabaseService.insert(ItemModels.getItem(invoice, product));

      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.UNPROCESSABLE_ENTITY, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("product_used", ErrorDto::getCode);
    }

    @Test
    void status204() {
      // arrange
      testDatabaseService.insert(ProductModels.getProduct());

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, getHttpHeadersWithBasicAuth()),
              UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
