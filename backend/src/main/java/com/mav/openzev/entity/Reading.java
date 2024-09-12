package com.mav.openzev.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
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
@Table(name = "OZEV_READINGS")
public class Reading extends AbstractEntity {

  // @OneToOne private MeterPoint meterPoint;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "from_peak_tariff")
  private double fromPeakTariff;

  @Column(name = "from_off_peak_tariff")
  private double fromOffPeakTariff;

  @Column(name = "from_total")
  private double fromTotal;

  @Column(name = "upto_peak_tariff")
  private double uptoPeakTariff;

  @Column(name = "upto_off_peak_tariff")
  private double uptoOffPeakTariff;

  @Column(name = "upto_total")
  private double uptoTotal;
}
