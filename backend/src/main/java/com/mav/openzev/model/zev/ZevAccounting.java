package com.mav.openzev.model.zev;

import com.mav.openzev.model.Agreement;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
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
@DiscriminatorValue("ZEV")
public class ZevAccounting extends com.mav.openzev.model.Accounting {

  @ManyToOne
  @JoinColumn(name = "AGREEMENT_ID")
  private Agreement agreement;

  @Digits(integer = 10, fraction = 2)
  @Column(name = "AMOUNT_HT")
  private BigDecimal amountHighTariff;

  @Digits(integer = 10, fraction = 2)
  @Column(name = "AMOUNT_LT")
  private BigDecimal amountLowTariff;
}
