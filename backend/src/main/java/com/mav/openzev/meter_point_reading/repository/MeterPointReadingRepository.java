package com.mav.openzev.meter_point_reading.repository;

import com.mav.openzev.common.exception.NotFoundException;
import com.mav.openzev.meter_point_reading.entity.MeterPointReading;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterPointReadingRepository extends JpaRepository<MeterPointReading, UUID> {

  default MeterPointReading findByIdOrFail(final UUID id) {
    return findById(id).orElseThrow(() -> NotFoundException.of(MeterPointReading.class, id));
  }

  @Override
  @Query(
      """
    select m from MeterPointReading m
    order by m.reading.date asc, m.meterPoint.number asc""")
  List<MeterPointReading> findAll();

  @Query(
      """
select m from MeterPointReading m
where m.reading.id = :#{#readingId}
order by m.reading.date asc, m.meterPoint.number asc
  """)
  List<MeterPointReading> findAllByReadingId(final UUID readingId);
}
