package com.mav.openzev.repository;

import com.mav.openzev.model.Unit;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

  Optional<Unit> findByUuid(UUID uuid);
}
