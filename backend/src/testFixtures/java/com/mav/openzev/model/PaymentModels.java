package com.mav.openzev.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("437b6478-a39f-41ce-a0b7-490458aeca5b");

  public static Payment getPayment(final Invoice invoice) {
    return Payment.builder()
        .uuid(UUID)
        .invoice(invoice)
        .amount(Constants.TWO)
        .received(Constants._2024_01_31)
        .build();
  }
}
