package com.mav.openzev.adapter.qrgenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;

import com.mav.openzev.adapter.qrgenerator.model.generator.*;
import com.mav.openzev.adapter.qrgenerator.model.validator.ValidationMessage;
import com.mav.openzev.exception.ValidationException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.approvaltests.JsonJacksonApprovals;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {QrGeneratorAdapter.class, QrGeneratorConfig.class})
class QrGeneratorAdapterTest {

  @Autowired private QrGeneratorAdapter sut;
  private static final MockWebServer mockWebServer = new MockWebServer();

  @SneakyThrows
  @BeforeAll
  static void beforeAll() {
    mockWebServer.start();
  }

  @SneakyThrows
  @AfterAll
  static void afterAll() {
    mockWebServer.shutdown();
  }

  @DynamicPropertySource
  static void config(final DynamicPropertyRegistry registry) {
    registry.add(
        "openzev.qr-generator.base-url",
        () -> "http://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort());
  }

  @Nested
  class ValidateTests {

    @Test
    void send_invalid_iban() {
      // arrange
      mockWebServer.enqueue(
          new MockResponse()
              .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .setResponseCode(HttpStatus.OK.value())
              .setBody(
                  """
{
  "rchBill": null,
  "validationMessages": [
    {
      "type": "ERROR",
      "field": "creditor.account",
      "messageKey": "account_is_valid_iban",
      "messageParameters": null
    }
  ]
}
"""));

      final Request request = RequestModels.get();

      // act
      final List<ValidationMessage> result = sut.validate(request);

      // assert
      assertThat(result)
          .hasSize(1)
          .singleElement()
          .returns("ERROR", ValidationMessage::type)
          .returns("creditor.account", ValidationMessage::field)
          .returns("account_is_valid_iban", ValidationMessage::messageKey)
          .returns(null, ValidationMessage::messageParameters);
    }
  }

  @Nested
  class GetTests {

    @Test
    void validation_failed() {
      // arrange
      mockWebServer.enqueue(
          new MockResponse()
              .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .setResponseCode(HttpStatus.OK.value())
              .setBody(
                  """
            {
              "rchBill": null,
              "validationMessages": [
                {
                  "type": "ERROR",
                  "field": "creditor.account",
                  "messageKey": "account_is_valid_iban",
                  "messageParameters": null
                }
              ]
            }
            """));

      final Request request = RequestModels.get();

      // act && assert
      assertThatThrownBy(() -> sut.get(request))
          .isInstanceOf(ValidationException.class)
          .asInstanceOf(throwable(ValidationException.class))
          .returns(
              "{ \"type\": ERROR, \"field\": creditor.account, \"messageKey\": account_is_valid_iban, \"messageParameters\": null }",
              ValidationException::getMessage);
    }

    @SneakyThrows
    @Test
    void get_qr_bill_as_pdf() {
      // arrange
      mockWebServer.enqueue(
          new MockResponse()
              .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .setResponseCode(HttpStatus.OK.value())
              .setBody(
                  """
                        {
                          "rchBill": null,
                          "validationMessages": []
                        }
                        """));

      mockWebServer.enqueue(
          new MockResponse()
              .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
              .setResponseCode(HttpStatus.OK.value())
              .setBody("Lorem ipsum"));

      final Request request = RequestModels.get();

      // act
      final byte[] result = sut.get(request);

      // assert
      assertThat(result).isNotEmpty();

      mockWebServer.takeRequest(); // ignore validate request
      final RecordedRequest recordedBillRequest = mockWebServer.takeRequest();
      JsonJacksonApprovals.verifyAsJson(
          recordedBillRequest.getBody().readString(StandardCharsets.UTF_8));
    }
  }
}
