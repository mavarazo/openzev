package com.mav.openzev.repository;

import com.mav.openzev.entity.MeterPoint;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterPointRepository extends JpaRepository<MeterPoint, UUID> {}
