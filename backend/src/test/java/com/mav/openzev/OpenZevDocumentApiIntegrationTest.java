package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.model.AccountingModels;
import com.mav.openzev.model.Document;
import com.mav.openzev.model.DocumentModels;
import com.mav.openzev.model.Property;
import com.mav.openzev.model.PropertyModels;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OpenZevDocumentApiIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private com.mav.openzev.TestDatabaseService testDatabaseService;

  @AfterEach
  void tearDown() {
    testDatabaseService.truncateAll();
  }

  @Nested
  class GetDocumentTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.documents(DocumentModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("document_not_found", ErrorDto::getCode);
    }

    @Test
    @SneakyThrows
    void status200() {
      // arrange
      final Property property =
          testDatabaseService.insertProperty(
              PropertyModels.getProperty().addAccounting(AccountingModels.getAccounting()));

      final Document document =
          property.getAccountings().stream()
              .findFirst()
              .map(a -> testDatabaseService.insertDocument(DocumentModels.getDocument(a)))
              .orElseThrow(IllegalArgumentException::new);

      final HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);

      // act
      final ResponseEntity<Resource> response =
          restTemplate.exchange(
              UriFactory.documents(DocumentModels.UUID),
              HttpMethod.GET,
              new HttpEntity<>(null, headers),
              Resource.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns(
              "lorem ipsum",
              b -> {
                try {
                  return b.getContentAsString(StandardCharsets.UTF_8);
                } catch (final IOException e) {
                  throw new RuntimeException(e);
                }
              });
    }
  }

  @Nested
  class DeleteDocumentTests {

    @Test
    void status404() {
      // act
      final ResponseEntity<ErrorDto> response =
          restTemplate.exchange(
              UriFactory.documents(DocumentModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("document_not_found", ErrorDto::getCode);
    }

    @Test
    @SneakyThrows
    void status200() {
      // arrange
      final Property property =
          testDatabaseService.insertProperty(
              PropertyModels.getProperty().addAccounting(AccountingModels.getAccounting()));

      final Document document =
          property.getAccountings().stream()
              .findFirst()
              .map(a -> testDatabaseService.insertDocument(DocumentModels.getDocument(a)))
              .orElseThrow(IllegalArgumentException::new);

      // act
      final ResponseEntity<Resource> response =
          restTemplate.exchange(
              UriFactory.documents(DocumentModels.UUID),
              HttpMethod.DELETE,
              new HttpEntity<>(null, null),
              Resource.class);

      // assert
      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    }
  }
}
