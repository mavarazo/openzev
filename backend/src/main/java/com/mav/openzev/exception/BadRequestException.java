package com.mav.openzev.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {
  @Getter private static final HttpStatus statusCode = HttpStatus.BAD_REQUEST;

  @Getter private final String code;

  private BadRequestException(final String code, final String message) {
    super(message);
    this.code = code;
  }

  public static BadRequestException ofMultipartFileRequired() {
    return new BadRequestException("multipart_file_required", "Multipart file is required.");
  }

  public static BadRequestException ofUnsupportedMediaType(final String contentType) {
    return new BadRequestException(
        "unsupported_media_type", "media type '%s' not supported.".formatted(contentType));
  }

  public static BadRequestException ofUnknownMediaType(final String contentType) {
    return new BadRequestException(
        "unknown_media_type", "media type '%s' unknown.".formatted(contentType));
  }
}
