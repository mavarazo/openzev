package com.mav.openzev.meter_point.repository;

import com.mav.openzev.common.exception.NotFoundException;
import com.mav.openzev.meter_point.entity.MeterPoint;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterPointRepository extends JpaRepository<MeterPoint, UUID> {

  default MeterPoint findByIdOrFail(final UUID id) {
    return findById(id).orElseThrow(() -> NotFoundException.of(MeterPoint.class, id));
  }
}
