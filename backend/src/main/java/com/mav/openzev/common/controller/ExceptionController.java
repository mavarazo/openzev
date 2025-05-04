package com.mav.openzev.common.controller;

import static java.util.Objects.requireNonNullElse;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.common.exception.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class ExceptionController {
  private static final Map<String, String> CONSTRAINT_VIOLATIONS;

  static {
    CONSTRAINT_VIOLATIONS = new HashMap<>();
    CONSTRAINT_VIOLATIONS.put(
        "{vendor-invoice-item.total.invalid}", "vendor-invoice-item.total.invalid");
  }

  private static final Map<String, String> DATA_INTEGRITY_VIOLATIONS;

  static {
    DATA_INTEGRITY_VIOLATIONS = new HashMap<>();
    DATA_INTEGRITY_VIOLATIONS.put("consumptions_date_key", "consumption_duplicated");
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<List<ErrorDto>> handleConstraintValidationExceptions(
      final ConstraintViolationException ex) {
    final List<ErrorDto> result = new ArrayList<>();
    for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      result.add(
          new ErrorDto()
              .code(CONSTRAINT_VIOLATIONS.get(violation.getMessage()))
              .message(violation.getMessage()));
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorDto> handleNotFoundException(final NotFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(ex.getStatusCode()).body(buildErrorDto(ex));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorDto> handleDataIntegrityViolationException(
      final DataIntegrityViolationException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto().code(evaluateCode(ex)));
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

  private String evaluateCode(final DataIntegrityViolationException ex) {
    final String violatedConstraint = getViolatedConstraint(ex);
    return DATA_INTEGRITY_VIOLATIONS.get(violatedConstraint);
  }

  private String getViolatedConstraint(final DataIntegrityViolationException ex) {
    return switch (ex.getCause()) {
      case final org.hibernate.exception.ConstraintViolationException c -> c.getConstraintName();
      default -> throw new IllegalStateException("Unexpected value: " + ex.getCause());
    };
  }
}
