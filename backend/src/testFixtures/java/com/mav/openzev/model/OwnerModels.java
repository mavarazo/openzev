package com.mav.openzev.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OwnerModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("790772bd-6425-41af-9270-297eb0d42060");

  public static Owner getOwner() {
    return Owner.builder()
        .uuid(UUID)
        .active(true)
        .firstName("Anna")
        .lastName("Barry")
        .email("anna@barry.com")
        .street("Stradun")
        .houseNr("30")
        .postalCode("1624")
        .city("Grattavache")
        .phoneNr("+41 41 555 66 77")
        .mobileNr("+41 79 555 66 77")
        .contractId("6aeeaf0f-7e55-4bb8-8fb1-10f24fb8318c")
        .build();
  }
}
