package com.mav.openzev.repository;

import com.mav.openzev.entity.MeterPoint;
import com.mav.openzev.entity.Reading;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {

  Optional<Reading> findByDateAndMeterPoint(LocalDate date, MeterPoint meterPoint);
}
