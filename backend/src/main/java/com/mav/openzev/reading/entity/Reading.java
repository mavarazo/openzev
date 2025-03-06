package com.mav.openzev.reading.entity;

import com.mav.openzev.common.entity.AbstractEntity;
import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "readings")
public class Reading extends AbstractEntity {

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MeterPointReading> meterPointReadings;
}
