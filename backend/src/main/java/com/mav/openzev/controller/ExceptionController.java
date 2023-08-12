package com.mav.openzev.controller;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionController {

  @ExceptionHandler(value = {NotFoundException.class})
  static ResponseEntity<ErrorDto> handleNotFoundException(final NotFoundException exception) {
    return ResponseEntity.status(NotFoundException.getStatusCode())
        .body(new ErrorDto().code(exception.getCode()).message(exception.getMessage()));
  }

  @ExceptionHandler(value = {ValidationException.class})
  static ResponseEntity<ErrorDto> handleValidationException(final ValidationException exception) {
    return ResponseEntity.status(ValidationException.getStatusCode())
        .body(new ErrorDto().code(exception.getCode()).message(exception.getMessage()));
  }
}
