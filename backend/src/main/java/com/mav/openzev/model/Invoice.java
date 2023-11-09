package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OZEV_INVOICES")
public class Invoice extends AbstractAuditEntity {

  @ManyToOne
  @JoinColumn(name = "ACCOUNTING_ID", nullable = false)
  private Accounting accounting;

  @ManyToOne
  @JoinColumn(name = "UNIT_ID", nullable = false)
  private Unit unit;

  @Column(name = "USAGE_HT")
  private Double usageHighTariff;

  @Column(name = "USAGE_LT")
  private Double usageLowTariff;

  @Positive
  @Column(name = "USAGE_TOTAL")
  private Double usageTotal;

  @Digits(integer = 5, fraction = 2)
  @Column(name = "AMOUNT_HT")
  private BigDecimal amountHighTariff;

  @Digits(integer = 5, fraction = 2)
  @Column(name = "AMOUNT_LT")
  private BigDecimal amountLowTariff;

  @Positive
  @Digits(integer = 5, fraction = 2)
  @Column(name = "AMOUNT_TOTAL")
  private BigDecimal amountTotal;

  @Column(name = "PAYED")
  private LocalDate payed;
}
