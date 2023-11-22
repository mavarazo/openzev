package com.mav.openzev;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.util.UriComponentsBuilder;

public class UriFactory {

  public static URI owners() {
    return UriComponentsBuilder.fromPath("/v1/owners").build().toUri();
  }

  public static URI owners(final String ownerId) {
    return UriComponentsBuilder.fromPath("/v1/owners/{ownerId}").buildAndExpand(ownerId).toUri();
  }

  public static URI properties() {
    return UriComponentsBuilder.fromPath("/v1/properties").build().toUri();
  }

  public static URI properties(final String propertyId) {
    return UriComponentsBuilder.fromPath("/v1/properties/{propertyId}")
        .buildAndExpand(propertyId)
        .toUri();
  }

  public static URI properties_accountings(final UUID propertyId) {
    return UriComponentsBuilder.fromPath("/v1/properties/{propertyId}/accountings")
        .buildAndExpand(propertyId)
        .toUri();
  }

  public static URI properties_agreements(final UUID propertyId) {
    return UriComponentsBuilder.fromPath("/v1/properties/{propertyId}/agreements")
        .buildAndExpand(propertyId)
        .toUri();
  }

  public static URI properties_owners(final UUID propertyId) {
    return UriComponentsBuilder.fromPath("/v1/properties/{propertyId}/owners")
        .buildAndExpand(propertyId)
        .toUri();
  }

  public static URI properties_units(final UUID propertyId) {
    return UriComponentsBuilder.fromPath("/v1/properties/{propertyId}/units")
        .buildAndExpand(propertyId)
        .toUri();
  }

  public static URI units(final UUID unitId) {
    return UriComponentsBuilder.fromPath("/v1/units/{unitId}").buildAndExpand(unitId).toUri();
  }

  public static URI units_ownerships(final String unitId) {
    return UriComponentsBuilder.fromPath("/v1/units/{unitId}/ownerships")
        .buildAndExpand(unitId)
        .toUri();
  }

  public static URI units_ownerships(
      final String unitId, final LocalDate validFrom, final LocalDate validUpto) {
    return UriComponentsBuilder.fromPath("/v1/units/{unitId}/ownerships")
        .queryParamIfPresent("validFrom", Optional.ofNullable(validFrom))
        .queryParamIfPresent("validUpto", Optional.ofNullable(validUpto))
        .buildAndExpand(unitId)
        .toUri();
  }

  public static URI ownerships() {
    return UriComponentsBuilder.fromPath("/v1/ownerships").build().toUri();
  }

  public static URI ownerships(final String ownershipId) {
    return UriComponentsBuilder.fromPath("/v1/ownerships/{ownershipId}")
        .buildAndExpand(ownershipId)
        .toUri();
  }

  public static URI agreements(final UUID agreementId) {
    return UriComponentsBuilder.fromPath("/v1/agreements/{agreementId}")
        .buildAndExpand(agreementId)
        .toUri();
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

  public static URI invoices() {
    return UriComponentsBuilder.fromPath("/v1/invoices").build().toUri();
  }

  public static URI invoices(final UUID invoiceId) {
    return UriComponentsBuilder.fromPath("/v1/invoices/{invoiceId}")
        .buildAndExpand(invoiceId)
        .toUri();
  }

  public static URI documents(final UUID documentId) {
    return UriComponentsBuilder.fromPath("/v1/documents/{documentId}")
        .buildAndExpand(documentId)
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
}
