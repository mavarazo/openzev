package com.mav.openzev.controller;

import com.mav.openzev.api.model.ErrorDto;
import com.mav.openzev.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionController {

  @ExceptionHandler(value = {NotFoundException.class})
  static ResponseEntity<ErrorDto> handleNotFoundEx(final NotFoundException notFoundEx) {
    return ResponseEntity.status(notFoundEx.getStatusCode())
        .body(new ErrorDto().code(notFoundEx.getCode()).message(notFoundEx.getMessage()));
  }
}
