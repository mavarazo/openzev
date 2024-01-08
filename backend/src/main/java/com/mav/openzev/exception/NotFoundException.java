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

  public static NotFoundException ofDocumentNotFound(final UUID documentId) {
    return new NotFoundException(
        "document_not_found", "document with id '%s' not found".formatted(documentId.toString()));
  }

  public static NotFoundException ofInvoiceNotFound(final UUID invoiceId) {
    return new NotFoundException(
        "invoice_not_found", "invoice with id '%s' not found".formatted(invoiceId.toString()));
  }

  public static RuntimeException ofItemNotFound(final UUID itemId) {
    return new NotFoundException(
        "item_not_found", "item with id '%s' not found".formatted(itemId.toString()));
  }

  public static NotFoundException ofOwnerNotFound(final UUID ownerId) {
    return new NotFoundException(
        "owner_not_found", "owner with id '%s' not found".formatted(ownerId.toString()));
  }

  public static NotFoundException ofOwnershipNotFound(final UUID ownershipId) {
    return new NotFoundException(
        "ownership_not_found",
        "ownership with id '%s' not found".formatted(ownershipId.toString()));
  }

  public static RuntimeException ofPaymentNotFound(final UUID paymentId) {
    return new NotFoundException(
        "payment_not_found", "payment with id '%s' not found".formatted(paymentId.toString()));
  }

  public static NotFoundException ofProductNotFound(final UUID productId) {
    return new NotFoundException(
        "product_not_found", "product with id '%s' not found".formatted(productId.toString()));
  }

  public static NotFoundException ofUnitNotFound(final UUID unitId) {
    return new NotFoundException(
        "unit_not_found", "unit with id '%s' not found".formatted(unitId.toString()));
  }

  public static NotFoundException ofZevConfigNotFound() {
    return new NotFoundException("zev_config_not_found", "ZevConfig not found");
  }

  public static NotFoundException ofZevRepresentativeConfigNotFound() {
    return new NotFoundException(
        "zev_representative_config_not_found", "ZevRepresentativeConfig not found");
  }
}
