package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "OZEV_UNITS")
public class Unit extends AbstractEntity {

  @Column(name = "ACTIVE")
  @Builder.Default
  private boolean active = true;

  @Column(name = "SUBJECT")
  private String subject;

  @Column(name = "VALUE_RATIO")
  private Integer valueRatio;

  @Column(name = "MPAN")
  private String meterPointAdministrationNumber;

  @ManyToOne
  @JoinColumn(name = "PROPERTY_ID", nullable = false)
  private Property property;

  @OneToMany(mappedBy = "unit")
  private Set<Ownership> ownerships;

  @OneToMany(mappedBy = "unit")
  private Set<Invoice> invoices;

  public Unit addOwnership(final Ownership ownership) {
    ownerships.add(ownership);
    ownership.setUnit(this);
    return this;
  }
}
