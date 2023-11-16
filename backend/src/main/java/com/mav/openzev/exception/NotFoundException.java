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

  public static NotFoundException ofOwnerNotFound(final UUID ownerId) {
    return new NotFoundException(
        "owner_not_found", "owner with id '%s' not found".formatted(ownerId.toString()));
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

  public static NotFoundException ofOwnershipNotFound(final UUID ownershipId) {
    return new NotFoundException(
        "ownership_not_found",
        "ownership with id '%s' not found".formatted(ownershipId.toString()));
  }

    public static NotFoundException ofAgreementNotFound(final UUID agreementId) {
        return new NotFoundException(
                "agreement_not_found",
                "agreement with id '%s' not found".formatted(agreementId.toString()));
    }

  public static NotFoundException ofDocumentNotFound(final UUID documentId) {
    return new NotFoundException(
        "document_not_found", "document with id '%s' not found".formatted(documentId.toString()));
    }

  public static RuntimeException ofPropertyNotFound(final UUID propertyId) {
    return new NotFoundException(
        "property_not_found", "property with id '%s' not found".formatted(propertyId.toString()));
  }
}
