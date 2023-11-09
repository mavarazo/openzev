package com.mav.openzev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
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
@Table(name = "OZEV_OWNERSHIPS")
public class Ownership extends AbstractAuditEntity {

  @ManyToOne
  @JoinColumn(name = "UNIT_ID", nullable = false)
  private Unit unit;

  @ManyToOne
  @JoinColumn(name = "USER_ID", nullable = false)
  private User user;

  @Column(name = "PERIOD_FROM", nullable = false)
  private LocalDate periodFrom;

  @Column(name = "PERIOD_UPTO")
  private LocalDate periodUpto;
}
