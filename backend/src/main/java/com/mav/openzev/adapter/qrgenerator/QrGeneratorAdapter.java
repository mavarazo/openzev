package com.mav.openzev.adapter.qrgenerator;

import static java.util.Objects.nonNull;

import com.mav.openzev.adapter.qrgenerator.model.generator.Format;
import com.mav.openzev.adapter.qrgenerator.model.generator.Request;
import com.mav.openzev.adapter.qrgenerator.model.validator.Response;
import com.mav.openzev.adapter.qrgenerator.model.validator.ValidationMessage;
import com.mav.openzev.exception.ValidationException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrGeneratorAdapter {

  private final RestClient qrGeneratorClient;

  public List<ValidationMessage> validate(final Request request) {
    final List<ValidationMessage> validationMessages = Collections.emptyList();

    final ResponseEntity<Response> response =
        qrGeneratorClient
            .post()
            .uri("/bill/validate")
            .body(request)
            .retrieve()
            .toEntity(Response.class);

    final Response body = response.getBody();
    if (nonNull(body)) {
      return body.validationMessages();
    }
    return validationMessages;
  }

  private void assertValidate(final Request request) {
    final List<ValidationMessage> validationMessages = validate(request);
    if (!validationMessages.isEmpty()) {
      validationMessages.forEach(
          validationMessage ->
              log.warn(
                  "received validation error: type: '{}', field: '{}', key: '{}', parameters: '{}'",
                  validationMessage.type(),
                  validationMessage.field(),
                  validationMessage.messageKey(),
                  validationMessage.messageParameters()));

      throw ValidationException.ofQrGeneratorValidationFailed(validationMessages);
    }
  }

  @SneakyThrows
  public byte[] get(final Request request) {
    assertValidate(request);

    final String uri =
        request.format().getGraphicsFormat() == Format.GraphicsFormat.PDF
            ? "/bill/image/pdf"
            : "/bill/image";

    final ResponseEntity<byte[]> response =
        qrGeneratorClient
            .post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(byte[].class);

    final byte[] body = response.getBody();
    if (HttpStatus.OK == response.getStatusCode() && nonNull(body)) {
      return body;
    }
    return new byte[0];
  }
}
