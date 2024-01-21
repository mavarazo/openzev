package com.mav.openzev.model;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("4db55aea-203b-4d19-a8c7-abaa8cff39e8");

  public static Item getItem(final Invoice invoice) {
    return Item.builder()
        .uuid(UUID)
        .invoice(invoice)
        .product(null)
        .quantity(Constants.ONE)
        .price(BigDecimal.ONE)
        .amount(BigDecimal.ONE)
        .build();
  }

  public static Item getItem(final Invoice invoice, final Product product) {
    return Item.builder()
        .uuid(UUID)
        .invoice(invoice)
        .product(product)
        .quantity(Constants.ONE)
        .price(product.getPrice())
        .amount(product.getPrice().multiply(BigDecimal.valueOf(Constants.ONE)))
        .build();
  }
}
