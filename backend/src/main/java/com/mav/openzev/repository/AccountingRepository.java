package com.mav.openzev.repository;

import com.mav.openzev.model.Accounting;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingRepository extends JpaRepository<Accounting, Long> {

  Optional<Accounting> findByUuid(UUID uuid);

  List<Accounting> findByProperty_Uuid(UUID propertyUuid, Sort sort);
}
