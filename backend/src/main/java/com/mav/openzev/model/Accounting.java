package com.mav.openzev.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
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
@Table(name = "OZEV_ACCOUNTINGS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("DEFAULT")
public class Accounting extends AbstractAuditEntity {

  @Column(name = "PERIOD_FROM", nullable = false)
  private LocalDate periodFrom;

  @Column(name = "PERIOD_UPTO", nullable = false)
  private LocalDate periodUpto;

  @Column(name = "SUBJECT")
  private String subject;

  @Positive
  @Digits(integer = 10, fraction = 2)
  @Column(name = "AMOUNT_TOTAL", nullable = false)
  private BigDecimal amountTotal;

  @OneToMany(mappedBy = "accounting", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Invoice> invoices = new HashSet<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "REF_ID", referencedColumnName = "ID")
  @Builder.Default
  private Set<Document> documents = new HashSet<>();

  public Accounting addInvoice(final Invoice invoice) {
    invoices.add(invoice);
    invoice.setAccounting(this);
    return this;
  }

  public Accounting addDocument(final Document document) {
    this.documents.add(document);
    document.setRefId(this.getId());
    document.setRefType(this.getClass().getSimpleName());
    return this;
  }
}
