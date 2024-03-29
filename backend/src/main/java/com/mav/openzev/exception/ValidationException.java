package com.mav.openzev.exception;


import com.mav.openzev.model.Owner;
import com.mav.openzev.model.Unit;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ValidationException extends RuntimeException {

  @Getter private static final HttpStatus statusCode = HttpStatus.UNPROCESSABLE_ENTITY;

  @Getter private final String code;

  private ValidationException(final String code, final String message) {
    super(message);
    this.code = code;
  }

  public static ValidationException ofOwnerHasOwnership(final Owner owner) {
    return new ValidationException(
        "owner_has_ownership",
        "owner with id '%s' is in use by ownership(s)".formatted(owner.getUuid().toString()));
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
}
