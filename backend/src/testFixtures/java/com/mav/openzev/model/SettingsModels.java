package com.mav.openzev.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettingsModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("242fcc40-eb81-4ab4-9505-a9a964c0b2dd");
  public static final String ZEV_STRADUN_30_GRATTAVACHE = "ZEV Stradun 30 Grattavache";
  public static final String STRADUN = "Stradun";
  public static final String _30 = "30";
  public static final String _1624 = "1624";
  public static final String GRATTAVACHE = "Grattavache";
  public static final String _F1DF95E0 = "f1df95e0-a1a0-4dd5-9001-4b196dd4b230";

  public static Settings getSettings() {
    return Settings.builder()
        .uuid(UUID)
        .name(ZEV_STRADUN_30_GRATTAVACHE)
        .street(STRADUN)
        .houseNr(_30)
        .postalCode(_1624)
        .city(GRATTAVACHE)
        .propertyNr(_F1DF95E0)
        .build();
  }
}
