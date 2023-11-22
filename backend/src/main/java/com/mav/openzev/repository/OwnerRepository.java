package com.mav.openzev.repository;

import com.mav.openzev.model.Owner;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

  Optional<Owner> findByUuid(UUID uuid);

  List<Owner> findByProperty_Uuid(UUID propertyUuid, Sort sort);
}
