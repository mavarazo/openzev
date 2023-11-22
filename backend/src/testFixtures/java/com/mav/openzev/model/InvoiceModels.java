package com.mav.openzev.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvoiceModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("414d2033-3b17-4e68-b69e-e483db0dc90b");

  public static Invoice getInvoice() {
    return Invoice.builder()
        .uuid(UUID)
        .usageHighTariff(1000.0)
        .usageLowTariff(750.0)
        .usageTotal(1750.0)
        .amountHighTariff(BigDecimal.valueOf(100.00))
        .amountLowTariff(BigDecimal.valueOf(75.00))
        .amountTotal(BigDecimal.valueOf(175.00))
        .payed(LocalDate.of(2023, 6, 1))
        .build();
  }
}
