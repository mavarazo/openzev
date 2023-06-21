package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "OZEV_UNITS")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Unit extends AbstractEntity {

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;

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
