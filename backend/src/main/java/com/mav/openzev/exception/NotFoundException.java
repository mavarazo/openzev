package com.mav.openzev.exception;

import java.util.UUID;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {

  @Getter private static final HttpStatus statusCode = HttpStatus.NOT_FOUND;

  @Getter private final String code;

  private NotFoundException(final String code, final String message) {
    super(message);
    this.code = code;
  }

  public static NotFoundException ofUserNotFound(final UUID userId) {
    return new NotFoundException(
        "user_not_found", "user with id '%s' not found".formatted(userId.toString()));
  }

  public static NotFoundException ofUnitNotFound(final UUID unitId) {
    return new NotFoundException(
        "unit_not_found", "unit with id '%s' not found".formatted(unitId.toString()));
  }

  public static NotFoundException ofAccountingNotFound(final UUID accountingId) {
    return new NotFoundException(
        "accounting_not_found",
        "accounting with id '%s' not found".formatted(accountingId.toString()));
  }

  public static NotFoundException ofInvoiceNotFound(final UUID invoiceId) {
    return new NotFoundException(
        "invoice_not_found", "invoice with id '%s' not found".formatted(invoiceId.toString()));
  }
}
