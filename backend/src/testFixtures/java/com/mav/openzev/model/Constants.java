package com.mav.openzev.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

  public static final BigDecimal TWO = BigDecimal.valueOf(2);
  public static final Float ONE = 1f;
  public static final LocalDate _2024_01_31 = LocalDate.of(2024, 1, 31);
}
