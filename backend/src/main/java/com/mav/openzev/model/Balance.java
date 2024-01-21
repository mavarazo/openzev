package com.mav.openzev.model;

import java.math.BigDecimal;

public record Balance(BigDecimal invoicedAmount, BigDecimal amountPaid, BigDecimal balance) {
  public Balance(final BigDecimal invoicedAmount, final BigDecimal amountPaid) {
    this(invoicedAmount, amountPaid, amountPaid.subtract(invoicedAmount));
  }
}
