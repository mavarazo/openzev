package com.mav.openzev.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "OZEV_INVOICES")
public class Invoice extends AbstractAuditEntity {

  @ManyToOne
  @JoinColumn(name = "UNIT_ID")
  private Unit unit;

  @ManyToOne
  @JoinColumn(name = "RECIPIENT_ID", nullable = false)
  private Owner recipient;

  @Column(name = "STATUS")
  @Enumerated(EnumType.STRING)
  private InvoiceStatus status;

  @Column(name = "DIRECTION", nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private InvoiceDirection direction = InvoiceDirection.OUTGOING;

  @Column(name = "TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private InvoiceType type = InvoiceType.STANDARD;

  @Column(name = "SUBJECT", nullable = false)
  private String subject;

  @Column(name = "NOTES")
  private String notes;

  @Column(name = "DUE_DATE", nullable = false)
  private LocalDate dueDate;

  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Item> items = new HashSet<>();

  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Payment> payments = new HashSet<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "REF_ID", referencedColumnName = "ID")
  @Builder.Default
  private Set<Document> documents = new HashSet<>();

  public void addItem(final Item item) {
    item.setInvoice(this);
    items.add(item);
  }
}
