package com.mav.openzev.reading.repository;

import com.mav.openzev.common.exception.NotFoundException;
import com.mav.openzev.reading.entity.Reading;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {

  default Reading findByIdOrFail(final UUID id) {
    return findById(id).orElseThrow(() -> NotFoundException.of(Reading.class, id));
  }
}
