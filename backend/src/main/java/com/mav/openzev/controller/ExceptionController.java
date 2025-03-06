package com.mav.openzev.controller;

import static java.util.Objects.requireNonNullElse;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationExcpetion;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class ExceptionController {

  private static final Map<String, String> CONSTRAINT_NAMES_TO_CODES;

  static {
    CONSTRAINT_NAMES_TO_CODES = new HashMap<>();
    CONSTRAINT_NAMES_TO_CODES.put("readings_date_meter_point_id_key", "reading_duplicated");
  }

  private static ResponseEntity<ErrorDto> defaultErrorResponse(final ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(buildErrorDto(ex));
  }

  private static ErrorDto buildErrorDto(final ResponseStatusException ex) {
    return new ErrorDto()
        .message(ex.getMessage())
        .code(ex.getDetailMessageCode())
        .args(
            Stream.of(requireNonNullElse(ex.getDetailMessageArguments(), new String[] {}))
                .map(Object::toString)
                .toList());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorDto> handleNotFoundException(final NotFoundException ex) {
    log.warn(ex.getMessage());
    return defaultErrorResponse(ex);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorDto> handleDataIntegrityViolationException(
      final DataIntegrityViolationException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto().code(evaluateCode(ex)));
  }

  private String evaluateCode(final DataIntegrityViolationException ex) {
    final String violatedConstraint = getViolatedConstraint(ex);
    return CONSTRAINT_NAMES_TO_CODES.get(violatedConstraint);
  }

  private String getViolatedConstraint(final DataIntegrityViolationException ex) {
    return switch (ex.getCause()) {
      case final ConstraintViolationException c -> c.getConstraintName();
      default -> throw new IllegalStateException("Unexpected value: " + ex.getCause());
    };
  }

  @ExceptionHandler(ValidationExcpetion.class)
  public ResponseEntity<ErrorDto> handleValidationException(final ValidationExcpetion ex) {
    log.warn(ex.getMessage());
    return defaultErrorResponse(ex);
  }
}
