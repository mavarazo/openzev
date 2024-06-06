package com.mav.openzev;

import java.net.URI;
import java.util.UUID;
import org.springframework.web.util.UriComponentsBuilder;

public class UriFactory {
  public static URI invoices() {
    return UriComponentsBuilder.fromPath("/v1/invoices").build().toUri();
  }

  public static URI invoices(final UUID invoiceId) {
    return UriComponentsBuilder.fromPath("/v1/invoices/{invoiceId}")
        .buildAndExpand(invoiceId)
        .toUri();
  }

  public static URI invoices_items(final UUID invoiceId) {
    return UriComponentsBuilder.fromPath("/v1/invoices/{invoiceId}/items")
        .buildAndExpand(invoiceId)
        .toUri();
  }

  public static URI invoices_payments(final UUID invoiceId) {
    return UriComponentsBuilder.fromPath("/v1/invoices/{invoiceId}/payments")
        .buildAndExpand(invoiceId)
        .toUri();
  }

  public static URI invoices_pdf(final UUID invoiceId) {
    return UriComponentsBuilder.fromPath("/v1/invoices/{invoiceId}/pdf")
        .buildAndExpand(invoiceId)
        .toUri();
  }

  public static URI invoices_email(final UUID invoiceId) {
    return UriComponentsBuilder.fromPath("/v1/invoices/{invoiceId}/email")
        .buildAndExpand(invoiceId)
        .toUri();
  }

  public static URI items(final UUID itemId) {
    return UriComponentsBuilder.fromPath("/v1/items/{itemId}").buildAndExpand(itemId).toUri();
  }

  public static URI settings() {
    return UriComponentsBuilder.fromPath("/v1/settings").build().toUri();
  }

  public static URI settings_bank_accounts() {
    return UriComponentsBuilder.fromPath("/v1/settings/bank-accounts").build().toUri();
  }

  public static URI settings_bank_accounts(final UUID bankAccountId) {
    return UriComponentsBuilder.fromPath("/v1/settings/bank-accounts/{bankAccountId}")
        .buildAndExpand(bankAccountId)
        .toUri();
  }

  public static URI settings_representatives() {
    return UriComponentsBuilder.fromPath("/v1/settings/representatives").build().toUri();
  }

  public static URI settings_representatives(final UUID representativeId) {
    return UriComponentsBuilder.fromPath("/v1/settings/representatives/{representativeId}")
        .buildAndExpand(representativeId)
        .toUri();
  }

  public static URI documents(final UUID documentId) {
    return UriComponentsBuilder.fromPath("/v1/documents/{documentId}")
        .buildAndExpand(documentId)
        .toUri();
  }

  public static URI owners() {
    return UriComponentsBuilder.fromPath("/v1/owners").build().toUri();
  }

  public static URI owners(final UUID ownerId) {
    return UriComponentsBuilder.fromPath("/v1/owners/{ownerId}").buildAndExpand(ownerId).toUri();
  }

  public static URI payments(final UUID paymentId) {
    return UriComponentsBuilder.fromPath("/v1/payments/{paymentId}")
        .buildAndExpand(paymentId)
        .toUri();
  }

  public static URI products() {
    return UriComponentsBuilder.fromPath("/v1/products").build().toUri();
  }

  public static URI products(final UUID productId) {
    return UriComponentsBuilder.fromPath("/v1/products/{productId}")
        .buildAndExpand(productId)
        .toUri();
  }

  public static URI units() {
    return UriComponentsBuilder.fromPath("/v1/units").build().toUri();
  }

  public static URI units(final UUID unitId) {
    return UriComponentsBuilder.fromPath("/v1/units/{unitId}").buildAndExpand(unitId).toUri();
  }

  public static URI units_ownerships(final UUID unitId) {
    return UriComponentsBuilder.fromPath("/v1/units/{unitId}/ownerships")
        .buildAndExpand(unitId)
        .toUri();
  }

  public static URI ownerships(final UUID ownershipId) {
    return UriComponentsBuilder.fromPath("/v1/ownerships/{ownershipId}")
        .buildAndExpand(ownershipId)
        .toUri();
  }
}
