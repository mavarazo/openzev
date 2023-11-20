package com.mav.openzev.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountingModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("27bc46ee-4d28-492b-a849-e52dbc5ded1a");
  public static final String ABRECHNUNG_2024_ZAEHLER_1 = "Abrechnung 2024 - Zähler 1";

  public static final LocalDate _2024_01_01 = LocalDate.of(2024, 1, 1);
  public static final LocalDate _2024_12_31 = LocalDate.of(2024, 12, 31);

  public static Accounting getAccounting() {
    return Accounting.builder()
        .uuid(UUID)
        .subject(ABRECHNUNG_2024_ZAEHLER_1)
        .periodFrom(_2024_01_01)
        .periodUpto(_2024_12_31)
        .amountHighTariff(BigDecimal.valueOf(1000))
        .amountLowTariff(BigDecimal.valueOf(500))
        .amountTotal(BigDecimal.valueOf(1500))
        .build();
  }
}
