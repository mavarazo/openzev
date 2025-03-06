package com.mav.openzev.meter_point.entity;

import com.mav.openzev.common.entity.AbstractEntity;
import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import com.mav.openzev.unit.entity.Unit;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "meter_points")
public class MeterPoint extends AbstractEntity {

  @OneToOne(optional = false)
  @JoinColumn(name = "unit_id")
  private Unit unit;

  @Column(name = "number", nullable = false)
  private String number;

  @OneToMany(mappedBy = "meterPoint", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MeterPointReading> meterPointReadings;
}
