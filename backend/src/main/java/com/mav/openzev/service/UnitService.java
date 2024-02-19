package com.mav.openzev.service;

import static java.util.Objects.isNull;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.UnitRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitService {

  private final UnitRepository unitRepository;

  public Optional<Unit> findUnit(final UUID unitId) {
    return isNull(unitId)
        ? Optional.empty()
        : Optional.ofNullable(
            unitRepository
                .findByUuid(unitId)
                .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId)));
  }
}
