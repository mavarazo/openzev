package com.mav.openzev.repository;

import com.mav.openzev.model.Ownership;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnershipRepository extends JpaRepository<Ownership, Long> {

  Optional<Ownership> findByUuid(UUID uuid);

  List<Ownership> findByUnit_Uuid(UUID unitId, Sort sort);
}
