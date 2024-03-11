package com.mav.openzev.exception;

import com.mav.openzev.model.*;
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
        "unit_used",
        "unit with id '%s' is in use by invoice(s)".formatted(unit.getUuid().toString()));
  }

  public static ValidationException ofProductIsUsed(final Product product) {
    return new ValidationException(
        "product_used",
        "product with id '%s' is in use by item(s)".formatted(product.getUuid().toString()));
  }

  public static ValidationException ofInvoiceHasWrongStatus(
      final Invoice invoice, final InvoiceStatus invoiceStatus) {
    return new ValidationException(
        "invoice_has_wrong_status",
        "invoice has status '%s', expected: '%s'"
            .formatted(
                invoice.getStatus().toString().toLowerCase(),
                invoiceStatus.toString().toLowerCase()));
  }

  public static ValidationException ofRecipientOfInvoiceHasNoEmail(final Invoice invoice) {
    return new ValidationException(
        "recipient_of_invoice_has_no_email",
        "recipient '%s' '%s' of invoice '%s' has no email"
            .formatted(
                invoice.getRecipient().getFirstName(),
                invoice.getRecipient().getLastName(),
                invoice.getUuid()));
  }
}
