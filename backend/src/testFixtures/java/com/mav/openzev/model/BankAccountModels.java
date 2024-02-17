package com.mav.openzev.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BankAccountModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("370f9494-8f2a-4c52-9b6e-5509415d0e4a");

  public static BankAccount getBankAccount() {
    return BankAccount.builder()
        .uuid(UUID)
        .active(true)
        .iban("ec191634-b482-4f1a-a5c8-ee825a6f7f45")
        .name("Dictumsollicitudin")
        .street("Habitasseadipiscing")
        .houseNr("146")
        .postalCode("6710")
        .city("Dignissimsem")
        .build();
  }
}
