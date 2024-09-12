package com.mav.openzev.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotFoundException extends ResponseStatusException {

  public NotFoundException(
      final String reason, final String messageDetailCode, final String... messageDetailArgs) {
    super(HttpStatus.NOT_FOUND, reason, null, messageDetailCode, messageDetailArgs);
  }

  public static NotFoundException meterPointNotFound(final UUID id) {
    return new NotFoundException(
        "Meter point with id '%s' not found".formatted(id), "not_found_meter_point");
  }
}
