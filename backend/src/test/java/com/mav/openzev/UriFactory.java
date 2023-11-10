package com.mav.openzev;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.web.util.UriComponentsBuilder;

public class UriFactory {

  public static URI users() {
    return UriComponentsBuilder.fromPath("/v1/users").build().toUri();
  }

  public static URI users(final String userId) {
    return UriComponentsBuilder.fromPath("/v1/users/{userId}").buildAndExpand(userId).toUri();
  }

  public static URI units() {
    return UriComponentsBuilder.fromPath("/v1/units").build().toUri();
  }

  public static URI units(final String unitId) {
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

  public static URI ownerships(final String ownershipId) {
    return UriComponentsBuilder.fromPath("/v1/ownerships/{unitId}")
        .buildAndExpand(ownershipId)
        .toUri();
  }

  public static URI agreements() {
    return UriComponentsBuilder.fromPath("/v1/agreements").build().toUri();
  }

  public static URI agreements(final String agreementId) {
    return UriComponentsBuilder.fromPath("/v1/agreements/{agreementId}")
        .buildAndExpand(agreementId)
        .toUri();
  }

  public static URI accountings() {
    return UriComponentsBuilder.fromPath("/v1/accountings").build().toUri();
  }

  public static URI accountings(final String accountingId) {
    return UriComponentsBuilder.fromPath("/v1/accountings/{accountingId}")
        .buildAndExpand(accountingId)
        .toUri();
  }

  public static URI accountings_documents(final String accountingId) {
    return UriComponentsBuilder.fromPath("/v1/accountings/{accountingId}/documents")
        .buildAndExpand(accountingId)
        .toUri();
  }

  public static URI invoices() {
    return UriComponentsBuilder.fromPath("/v1/invoices").build().toUri();
  }

  public static URI invoices(final String invoiceId) {
    return UriComponentsBuilder.fromPath("/v1/invoices/{invoiceId}")
        .buildAndExpand(invoiceId)
        .toUri();
  }

  public static URI documents(final String documentId) {
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

  public static URI dashboard_users() {
    return UriComponentsBuilder.fromPath("/v1/dashboard").path("/users").build().toUri();
  }
}
