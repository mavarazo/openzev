package com.mav.openzev.model;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OwnershipModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("44b62df3-3667-4af0-926d-8213a6874096");

  public static Ownership getOwnership(final Owner owner, final Unit unit) {
    return Ownership.builder()
        .uuid(UUID)
        .active(true)
        .owner(owner)
        .unit(unit)
        .periodFrom(LocalDate.of(2020, 1, 1))
        .build();
  }
}
