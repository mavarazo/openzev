package com.mav.openzev.vendor_invoice.entity;

import com.mav.openzev.common.entity.AbstractEntity;
import com.mav.openzev.vendor_invoice.validation.TotalConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "vendor_invoice_items")
@TotalConstraint
public class VendorInvoiceItem extends AbstractEntity {

  @ManyToOne
  @JoinColumn(name = "vendor_invoice_id")
  private VendorInvoice vendorInvoice;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private VendorInvoiceItemType type;

  @Column(name = "position")
  private int position;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "quantity", nullable = false)
  private Float quantity;

  @Column(name = "unit", nullable = false)
  private String unit;

  @Column(name = "price", nullable = false)
  private Float price;

  @Column(name = "total", nullable = false)
  private Float total;
}
