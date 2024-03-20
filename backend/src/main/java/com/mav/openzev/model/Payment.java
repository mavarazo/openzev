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

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OZEV_PAYMENTS")
public class Payment extends AbstractAuditEntity {

  @ManyToOne
  @JoinColumn(name = "INVOICE_ID", nullable = false)
  private Invoice invoice;

  @Digits(integer = 5, fraction = 2)
  @Positive
  @Column(name = "AMOUNT", nullable = false)
  private BigDecimal amount;

  @Column(name = "RECEIVED", nullable = false)
  private LocalDate received;

  @Column(name = "NOTES")
  private String notes;

  public void setInvoice(final Invoice invoice) {
    this.invoice = invoice;
    updateInvoice();
  }

  public void setAmount(final BigDecimal amount) {
    this.amount = amount;
    updateInvoice();
  }

  private void updateInvoice() {
    invoice.afterPayment();
  }
}
