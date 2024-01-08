package com.mav.openzev;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.model.DocumentModels;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
@Disabled("needs rework")
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
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("document_not_found", ErrorDto::getCode);
    }

    //    @Test
    //    @SneakyThrows
    //    void status200() {
    //      // arrange
    //      final Accounting accounting =
    // testDatabaseService.insert(AccountingModels.getAccounting());
    //      testDatabaseService.merge(accounting.addDocument(DocumentModels.getDocument()));
    //
    //      final HttpHeaders headers = new HttpHeaders();
    //      headers.setContentType(MediaType.APPLICATION_PDF);
    //
    //      // act
    //      final ResponseEntity<Resource> response =
    //          restTemplate.exchange(
    //              UriFactory.documents(DocumentModels.UUID),
    //              HttpMethod.GET,
    //              new HttpEntity<>(null, headers),
    //              Resource.class);
    //
    //      // assert
    //      assertThat(response)
    //          .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
    //          .extracting(ResponseEntity::getBody)
    //          .returns(
    //              "lorem ipsum",
    //              b -> {
    //                try {
    //                  return b.getContentAsString(StandardCharsets.UTF_8);
    //                } catch (final IOException e) {
    //                  throw new RuntimeException(e);
    //                }
    //              });
    //    }
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
              HttpEntity.EMPTY,
              ErrorDto.class);

      // assert
      assertThat(response)
          .returns(HttpStatus.NOT_FOUND, ResponseEntity::getStatusCode)
          .extracting(ResponseEntity::getBody)
          .returns("document_not_found", ErrorDto::getCode);
    }

    //    @Test
    //    @SneakyThrows
    //    void status200() {
    //      // arrange
    //      final Accounting accounting =
    // testDatabaseService.insert(AccountingModels.getAccounting());
    //      testDatabaseService.merge(accounting.addDocument(DocumentModels.getDocument()));
    //
    //      // act
    //      final ResponseEntity<Resource> response =
    //          restTemplate.exchange(
    //              UriFactory.documents(DocumentModels.UUID),
    //              HttpMethod.DELETE,
    //              HttpEntity.EMPTY,
    //              Resource.class);
    //
    //      // assert
    //      assertThat(response).returns(HttpStatus.NO_CONTENT, ResponseEntity::getStatusCode);
    //    }
  }
}
