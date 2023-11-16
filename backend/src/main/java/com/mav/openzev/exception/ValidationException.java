package com.mav.openzev.exception;

import static java.util.Objects.nonNull;

import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.model.Property;
import com.mav.openzev.model.Unit;
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

  public static ValidationException ofAccountingHasInvoice(final Accounting accounting) {
    return new ValidationException(
        "accounting_has_invoice",
        "accounting with id '%s' is in use by invoice(s)"
            .formatted(accounting.getUuid().toString()));
  }

  public static ValidationException ofAgreementHasAccounting(final Agreement agreement) {
    return new ValidationException(
        "agreement_has_accounting",
        "agreement with id '%s' is in use by accounting(s)"
            .formatted(agreement.getUuid().toString()));
  }

    public static ValidationException ofPropertyHasUnit(final Property property) {
        return new ValidationException(
                "property_has_unit",
                "property with id '%s' is in use by unit(s)"
                        .formatted(property.getUuid().toString()));
    }
}
