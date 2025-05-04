package com.mav.openzev.consumption.entity;

import com.mav.openzev.common.entity.AbstractEntity;
import com.mav.openzev.meter_point.entity.MeterPoint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Entity
@Table(
    name = "consumptions",
    indexes = @Index(columnList = "date"),
    uniqueConstraints = @UniqueConstraint(columnNames = {"meter_point_id", "date"}))
public class Consumption extends AbstractEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "meter_point_id")
  private MeterPoint meterPoint;

  @OneToOne
  @JoinColumn(name = "previous_consumption_id")
  private Consumption previousConsumption;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "total", nullable = false)
  @Builder.Default
  private BigDecimal total = BigDecimal.ZERO;
}
