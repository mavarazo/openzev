package com.mav.openzev.entity;

import jakarta.persistence.*;
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

  @Column(name = "number", nullable = false)
  private String number;

  @OneToMany(mappedBy = "meterPoint", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Reading> readings;
}
