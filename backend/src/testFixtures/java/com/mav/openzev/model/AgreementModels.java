package com.mav.openzev.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AgreementModels {

  public static final UUID UUID = java.util.UUID.fromString("aa2b617e-24eb-44cf-ba03-e4a45c297e54");
  public static final LocalDate _2024_01_01 = LocalDate.of(2024, 1, 1);
  public static final LocalDate _2024_12_31 = LocalDate.of(2024, 12, 31);
  public static final BigDecimal _0_25 = BigDecimal.valueOf(0.25);
  public static final BigDecimal _0_15 = BigDecimal.valueOf(0.15);
  public static final LocalDate _2023_10_01 = LocalDate.of(2023, 10, 1);

  public static Agreement getAgreement() {
    return Agreement.builder()
        .uuid(UUID)
        .property(PropertyModels.getProperty())
        .periodFrom(_2024_01_01)
        .periodUpto(_2024_12_31)
        .highTariff(_0_25)
        .lowTariff(_0_15)
        .approved(_2023_10_01)
        .build();
  }
}
