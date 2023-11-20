package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OZEV_AGREEMENTS")
public class Agreement extends AbstractAuditEntity {

  @Column(name = "ACTIVE")
  @Builder.Default
  private boolean active = true;

  @Column(name = "PERIOD_FROM", nullable = false)
  private LocalDate periodFrom;

  @Column(name = "PERIOD_UPTO", nullable = false)
  private LocalDate periodUpto;

  @Digits(integer = 5, fraction = 2)
  @Column(name = "HIGH_TARIFF", nullable = false)
  private BigDecimal highTariff;

  @Digits(integer = 5, fraction = 2)
  @Column(name = "LOW_TARIFF")
  private BigDecimal lowTariff;

  @Column(name = "APPROVED", nullable = false)
  private LocalDate approved;

  @OneToMany(mappedBy = "agreement")
  @Builder.Default
  private Set<Accounting> accountings = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "PROPERTY_ID", nullable = false)
  private Property property;

  public void addAccounting(final Accounting accounting) {
    accountings.add(accounting);
    accounting.setAgreement(this);
  }
}
