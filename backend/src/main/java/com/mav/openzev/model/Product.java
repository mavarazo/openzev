package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
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
@Table(name = "OZEV_PRODUCTS")
public class Product extends AbstractAuditEntity {

  @Column(name = "ACTIVE")
  @Builder.Default
  private boolean active = true;

  @Column(name = "SUBJECT", nullable = false)
  private String subject;

  @Column(name = "NOTES")
  private String notes;

  @Digits(integer = 5, fraction = 2)
  @Positive
  @Column(name = "PRICE", nullable = false)
  private BigDecimal price;
}
