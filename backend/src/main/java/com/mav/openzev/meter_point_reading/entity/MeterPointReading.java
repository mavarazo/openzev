package com.mav.openzev.meter_point_reading.entity;

import com.mav.openzev.common.entity.AbstractEntity;
import com.mav.openzev.meter_point.entity.MeterPoint;
import com.mav.openzev.meter_point_reading.validation.TotalConstraint;
import com.mav.openzev.reading.entity.Reading;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "meter_point_readings")
@TotalConstraint
public class MeterPointReading extends AbstractEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "reading_id")
  private Reading reading;

  @ManyToOne(optional = false)
  @JoinColumn(name = "meter_point_id")
  private MeterPoint meterPoint;

  @Column(name = "peak_tariff", nullable = false)
  @Builder.Default
  private BigDecimal peakTariff = BigDecimal.ZERO;

  @Column(name = "off_peak_tariff", nullable = false)
  @Builder.Default
  private BigDecimal offPeakTariff = BigDecimal.ZERO;

  @Column(name = "total", nullable = false)
  @Builder.Default
  private BigDecimal total = BigDecimal.ZERO;
}
