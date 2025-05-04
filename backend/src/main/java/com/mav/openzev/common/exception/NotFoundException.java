package com.mav.openzev.common.exception;

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

  public static <T> NotFoundException of(final Class<T> clazz, final UUID id) {
    return new NotFoundException(
        String.format(DEFAULT_MESSAGE, clazz.getSimpleName(), id),
        DEFAULT_CODE.formatted(clazz.getSimpleName().toLowerCase()),
        id.toString());
  }
}
