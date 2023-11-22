package com.mav.openzev.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UnitModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("414d2033-3b17-4e68-b69e-e483db0dc90b");

  public static Unit getUnit() {
    return Unit.builder()
        .uuid(UUID)
        .active(true)
        .subject("EG/1.OG rechts")
        .valueRatio(125)
        .meterPointAdministrationNumber("e4e2043a-05b1-4735-a5ea-8f972115df17")
        .build();
  }
}
