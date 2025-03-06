package com.mav.openzev.unit.repository;

import com.mav.openzev.common.exception.NotFoundException;
import com.mav.openzev.unit.entity.Unit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, UUID> {

  default Unit findByIdOrFail(final UUID id) {
    return findById(id).orElseThrow(() -> NotFoundException.of(Unit.class, id));
  }
}
