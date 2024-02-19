package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
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
@Table(name = "OZEV_ITEMS")
public class Item extends AbstractAuditEntity {

  @ManyToOne
  @JoinColumn(name = "INVOICE_ID", nullable = false)
  private Invoice invoice;

  @ManyToOne
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;

  @Column(name = "NOTES")
  private String notes;

  @Positive
  @Column(name = "QUANTITY", nullable = false)
  private float quantity;

  @Digits(integer = 5, fraction = 2)
  @Column(name = "PRICE", nullable = false)
  private BigDecimal price;

  @Digits(integer = 5, fraction = 2)
  @Column(name = "AMOUNT", nullable = false)
  private BigDecimal amount;
}
