package com.mav.openzev.repository;

import com.mav.openzev.model.Agreement;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {

  Optional<Agreement> findByUuid(UUID uuid);
}
