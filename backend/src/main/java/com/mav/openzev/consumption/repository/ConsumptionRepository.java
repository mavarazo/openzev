package com.mav.openzev.consumption.repository;

import com.mav.openzev.common.exception.NotFoundException;
import com.mav.openzev.consumption.entity.Consumption;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumptionRepository extends JpaRepository<Consumption, UUID> {

  List<Consumption> findAllByMeterPoint_Id(UUID meterPointId, Sort sort);

  default Consumption findByIdOrFail(final UUID id) {
    return findById(id).orElseThrow(() -> NotFoundException.of(Consumption.class, id));
  }
}
