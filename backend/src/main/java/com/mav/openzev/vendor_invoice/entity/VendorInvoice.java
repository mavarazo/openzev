package com.mav.openzev.vendor_invoice.entity;

import com.mav.openzev.common.entity.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "vendor_invoices")
public class VendorInvoice extends AbstractEntity {

  @Column(name = "state", nullable = false)
  @Enumerated(EnumType.STRING)
  private State state;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "period_from")
  private LocalDate periodFrom;

  @Column(name = "period_upto")
  private LocalDate periodUpto;

  @OneToMany(mappedBy = "vendorInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  List<VendorInvoiceItem> items = new ArrayList<>();

  public void addItem(final VendorInvoiceItem item) {
    item.setVendorInvoice(this);
    items.add(item);
  }
}
