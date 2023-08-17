package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "OZEV_ACCOUNTINGS")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Accounting extends AbstractAuditEntity {

  @ManyToOne
  @JoinColumn(name = "AGREEMENT_ID")
  private Agreement agreement;

  @Column(name = "PERIOD_FROM", nullable = false)
  private LocalDate periodFrom;

  @Column(name = "PERIOD_UPTO", nullable = false)
  private LocalDate periodUpto;

  @Column(name = "SUBJECT")
  private String subject;

  @Digits(integer = 5, fraction = 2)
  @Column(name = "AMOUNT_HT")
  private BigDecimal amountHighTariff;

  @Digits(integer = 5, fraction = 2)
  @Column(name = "AMOUNT_LT")
  private BigDecimal amountLowTariff;

  @Positive
  @Digits(integer = 5, fraction = 2)
  @Column(name = "AMOUNT_TOTAL", nullable = false)
  private BigDecimal amountTotal;

  @OneToMany(mappedBy = "accounting")
  private Set<Invoice> invoices;
}
