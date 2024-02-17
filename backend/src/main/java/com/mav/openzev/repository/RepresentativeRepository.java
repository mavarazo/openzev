package com.mav.openzev.repository;

import com.mav.openzev.model.Representative;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepresentativeRepository extends JpaRepository<Representative, Long> {

  Optional<Representative> findByUuid(UUID uuid);

  @Query("select r from Representative r")
  Optional<Representative> findActive();
}
