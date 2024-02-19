package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.api.model.ModifiableProductDto;
import com.mav.openzev.api.model.ProductDto;
import com.mav.openzev.helper.JsonJacksonApprovals;
import com.mav.openzev.helper.RequiredSource;
import com.mav.openzev.model.Constants;
import com.mav.openzev.model.ProductModels;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenZevProductApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private TestDatabaseService testDatabaseService;
  @Autowired private JsonJacksonApprovals jsonJacksonApprovals;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetProductsTests {

    @Test
    void status200() {
      // arrange
      testDatabaseService.insert(ProductModels.getProduct());

      // act
      final ResponseEntity<ProductDto[]> response =
          restTemplate.exchange(
              UriFactory.products(), HttpMethod.GET, HttpEntity.EMPTY, ProductDto[].class);

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
              HttpEntity.EMPTY,
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
              HttpEntity.EMPTY,
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
              new HttpEntity<>(requestBody, null),
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
              new HttpEntity<>(requestBody, null),
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
                  HttpEntity.EMPTY,
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
              new HttpEntity<>(requestBody, null),
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
              new HttpEntity<>(requestBody, null),
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
              new HttpEntity<>(requestBody, null),
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
                  HttpEntity.EMPTY,
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
              UriFactory.products(ProductModels.UUID), HttpMethod.DELETE, null, ErrorDto.class);

      // assert
      assertThat(response).returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode);
    }

    @Test
    void status204() {
      // arrange
      testDatabaseService.insert(ProductModels.getProduct());

      // act
      final ResponseEntity<UUID> response =
          restTemplate.exchange(
              UriFactory.products(ProductModels.UUID), HttpMethod.DELETE, null, UUID.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
