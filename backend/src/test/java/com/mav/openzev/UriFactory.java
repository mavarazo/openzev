package com.mav.openzev;

import java.net.URI;
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

  public static URI accountings() {
    return UriComponentsBuilder.fromPath("/v1/accountings").build().toUri();
  }

  public static URI accountings(final String accountingId) {
    return UriComponentsBuilder.fromPath("/v1/accountings/{accountingId}")
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
}
