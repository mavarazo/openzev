package com.mav.openzev;

import java.net.URI;
import java.util.UUID;
import org.springframework.web.util.UriComponentsBuilder;

public class UriFactory {

  public static URI accountings() {
    return UriComponentsBuilder.fromPath("/v1/accountings").build().toUri();
  }

  public static URI accountings(final UUID accountingId) {
    return UriComponentsBuilder.fromPath("/v1/accountings/{accountingId}")
        .buildAndExpand(accountingId)
        .toUri();
  }

  public static URI accountings_documents(final UUID accountingId) {
    return UriComponentsBuilder.fromPath("/v1/accountings/{accountingId}/documents")
        .buildAndExpand(accountingId)
        .toUri();
  }

  public static URI accountings_invoices(final UUID accountingId) {
    return UriComponentsBuilder.fromPath("/v1/accountings/{accountingId}/invoices")
        .buildAndExpand(accountingId)
        .toUri();
  }

  public static URI agreements() {
    return UriComponentsBuilder.fromPath("/v1/agreements").build().toUri();
  }

  public static URI agreements(final UUID agreementId) {
    return UriComponentsBuilder.fromPath("/v1/agreements/{agreementId}")
        .buildAndExpand(agreementId)
        .toUri();
  }

  public static URI dashboard_accountings() {
    return UriComponentsBuilder.fromPath("/v1/dashboard").path("/accountings").build().toUri();
  }

  public static URI dashboard_units() {
    return UriComponentsBuilder.fromPath("/v1/dashboard").path("/units").build().toUri();
  }

  public static URI dashboard_owners() {
    return UriComponentsBuilder.fromPath("/v1/dashboard").path("/owners").build().toUri();
  }

  public static URI documents(final UUID documentId) {
    return UriComponentsBuilder.fromPath("/v1/documents/{documentId}")
        .buildAndExpand(documentId)
        .toUri();
  }

  public static URI invoices(final UUID invoiceId) {
    return UriComponentsBuilder.fromPath("/v1/invoices/{invoiceId}")
        .buildAndExpand(invoiceId)
        .toUri();
  }

  public static URI owners() {
    return UriComponentsBuilder.fromPath("/v1/owners").build().toUri();
  }

  public static URI owners(final UUID ownerId) {
    return UriComponentsBuilder.fromPath("/v1/owners/{ownerId}").buildAndExpand(ownerId).toUri();
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
