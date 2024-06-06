package com.mav.openzev.adapter.qrgenerator;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import com.mav.openzev.adapter.qrgenerator.model.generator.*;
import com.mav.openzev.adapter.qrgenerator.model.validator.ValidationMessage;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {QrGeneratorAdapter.class, QrGeneratorConfig.class})
@EnableWireMock(@ConfigureWireMock(name = "qr-service", property = "openzev.qr-generator.base-url"))
class QrGeneratorAdapterTest {

  @InjectWireMock("qr-service")
  private WireMockServer wiremock;

  @Autowired private QrGeneratorAdapter sut;

  @Nested
  class ValidateTests {

    @Test
    void send_invalid_iban() {
      // arrange
      wiremock.stubFor(
          post(urlMatching("/bill/validate"))
              .willReturn(
                  okJson(
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
}""")));

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
}
