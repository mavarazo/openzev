package com.mav.openzev.service;

import static java.util.Objects.isNull;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.UnitRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnitService {

  private final UnitRepository unitRepository;

  public Unit findUnitOrFail(final UUID unitId) {
    return unitRepository
        .findByUuid(unitId)
        .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId));
  }

  public Optional<Unit> findUnit(final UUID unitId) {
    return isNull(unitId) ? Optional.empty() : Optional.ofNullable(findUnitOrFail(unitId));
  }

  @Transactional
  public void deleteUnit(final UUID unitId) {
    final Unit unit = findUnitOrFail(unitId);

    if (!unit.getOwnerships().isEmpty()) {
      throw ValidationException.ofUnitHasOwnership(unit);
    }

    if (!unit.getInvoices().isEmpty()) {
      throw ValidationException.ofUnitHasInvoice(unit);
    }

    unitRepository.delete(unit);
  }
}
