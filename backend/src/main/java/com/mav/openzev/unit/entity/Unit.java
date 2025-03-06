package com.mav.openzev.unit.entity;

import com.mav.openzev.common.entity.AbstractEntity;
import com.mav.openzev.meter_point.entity.MeterPoint;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "units")
public class Unit extends AbstractEntity {

  @OneToOne(mappedBy = "unit", cascade = CascadeType.ALL)
  private MeterPoint meterPoint;

  @Column(name = "number", nullable = false)
  private String number;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;
}
