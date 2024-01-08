package com.mav.openzev.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("370f9494-8f2a-4c52-9b6e-5509415d0e4a");

  public static Product getProduct() {
    return Product.builder()
        .uuid(UUID)
        .active(true)
        .subject("Sedsociosqu")
        .cost(Constants.TWO)
        .build();
  }
}
