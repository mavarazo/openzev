package com.mav.openzev.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "readings")
public class Reading extends AbstractEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "meter_point_id")
  private MeterPoint meterPoint;

  @Column(name = "date", nullable = false)
  private LocalDate date;

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
