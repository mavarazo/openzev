package com.mav.openzev.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotFoundException extends ResponseStatusException {

  private static final String DEFAULT_MESSAGE = "%s with id '%s' not found";
  private static final String DEFAULT_CODE = "%s_not_found";

  public NotFoundException(
      final String reason, final String messageDetailCode, final String... messageDetailArgs) {
    super(HttpStatus.NOT_FOUND, reason, null, messageDetailCode, messageDetailArgs);
  }

  public static NotFoundException meterPointNotFound(final UUID id) {
    return new NotFoundException(
        DEFAULT_MESSAGE.formatted("Meter point", id), DEFAULT_CODE.formatted("meter_point"));
  }

  public static NotFoundException readingNotFound(UUID id) {
    return new NotFoundException(
        DEFAULT_MESSAGE.formatted("Reading", id), DEFAULT_CODE.formatted("reading"));
  }
}
