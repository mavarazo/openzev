package com.mav.openzev.model;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertyModels {

  public static final UUID UUID = java.util.UUID.fromString("c3fbecd9-ea01-4d0b-8fa9-4b38f0e820d3");
  public static final String STREET = "Stradun";
  public static final String HOUSE_NR = "30";
  public static final String POSTAL_CODE = "1624";
  public static final String CITY = "Grattavache";

  public static Property getProperty() {
    return Property.builder()
        .uuid(UUID)
        .street(STREET)
        .houseNr(HOUSE_NR)
        .postalCode(POSTAL_CODE)
        .city(CITY)
        .build();
  }
}
