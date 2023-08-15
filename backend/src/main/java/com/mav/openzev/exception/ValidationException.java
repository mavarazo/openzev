package com.mav.openzev.exception;

import static java.util.Objects.nonNull;

import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.model.Unit;
import com.mav.openzev.model.User;
import java.util.StringJoiner;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ValidationException extends RuntimeException {

  @Getter private static final HttpStatus statusCode = HttpStatus.UNPROCESSABLE_ENTITY;

  @Getter private final String code;

  private ValidationException(final String code, final String message) {
    super(message);
    this.code = code;
  }

  public static ValidationException ofOwnershipOverlap(
      final Ownership ownership, final Ownership overlap) {

    final StringJoiner message = new StringJoiner(" ");
    message.add("ownership");
    if (nonNull(ownership.getUuid())) {
      message.add("id '%s'".formatted(ownership.getUuid()));
    }
    message.add(
        "'%s - %s'"
            .formatted(
                ownership.getPeriodFrom(),
                nonNull(ownership.getPeriodUpto()) ? ownership.getPeriodUpto() : ""));

    message.add("overlaps with");
    if (nonNull(ownership.getUuid())) {
      message.add("id '%s'".formatted(overlap.getUuid()));
    }
    message.add(
        "'%s - %s'"
            .formatted(
                overlap.getPeriodFrom(),
                nonNull(overlap.getPeriodUpto()) ? overlap.getPeriodUpto() : ""));
    return new ValidationException("ownership_overlap", message.toString());
  }

  public static ValidationException ofUserHasOwnership(final User user) {
    return new ValidationException(
        "user_has_ownership",
        "user with id '%s' is in use by ownership(s)".formatted(user.getUuid().toString()));
  }

  public static ValidationException ofUnitHasOwnership(final Unit unit) {
    return new ValidationException(
        "unit_has_ownership",
        "unit with id '%s' is in use by ownership(s)".formatted(unit.getUuid().toString()));
  }

  public static ValidationException ofUnitHasInvoice(final Unit unit) {
    return new ValidationException(
        "unit_has_invoice",
        "unit with id '%s' is in use by invoice(s)".formatted(unit.getUuid().toString()));
  }

  public static ValidationException ofAccountingHasInvoice(final Accounting accounting) {
    return new ValidationException(
        "accounting_has_invoice",
        "accounting with id '%s' is in use by invoice(s)"
            .formatted(accounting.getUuid().toString()));
  }
}
