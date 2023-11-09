package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OZEV_UNITS")
public class Unit extends AbstractEntity {

  @OneToMany(mappedBy = "unit")
  private Set<Ownership> ownerships;

  @Column(name = "ACTIVE")
  @Builder.Default
  private boolean active = true;

  @Column(name = "SUBJECT")
  private String subject;

  @Column(name = "VALUE_RATIO")
  private Integer valueRatio;

  @Column(name = "MPAN")
  private String meterPointAdministrationNumber;

  @OneToMany(mappedBy = "unit")
  private Set<Invoice> invoices;
}
