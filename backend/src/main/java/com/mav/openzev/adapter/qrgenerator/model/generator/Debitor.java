package com.mav.openzev.adapter.qrgenerator.model.generator;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Debitor {
  @NonNull private String name;
  private String street;
  private String houseNo;
  @Builder.Default private String countryCode = "CH";
  @NonNull private String postalCode;
  @NonNull private String town;
}
