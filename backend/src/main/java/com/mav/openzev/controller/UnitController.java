package com.mav.openzev.controller;

import com.mav.openzev.api.UnitApi;
import com.mav.openzev.api.model.ModifiableUnitDto;
import com.mav.openzev.api.model.UnitDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.UnitMapper;
import com.mav.openzev.model.Property;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.PropertyRepository;
import com.mav.openzev.repository.UnitRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UnitController implements UnitApi {

  private final UnitRepository unitRepository;
  private final PropertyRepository propertyRepository;

  private final UnitMapper unitMapper;

  @Override
  public ResponseEntity<List<UnitDto>> getUnits() {
    return ResponseEntity.ok(
        unitRepository.findAll(Sort.sort(Unit.class).by(Unit::getSubject)).stream()
            .map(unitMapper::mapToUnitDto)
            .toList());
  }

  @Override
  public ResponseEntity<UnitDto> getUnit(final UUID unitId) {
    return ResponseEntity.ok(
        unitRepository
            .findByUuid(unitId)
            .map(unitMapper::mapToUnitDto)
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId)));
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createUnit(final ModifiableUnitDto modifiableUnitDto) {
    final Property property =
        propertyRepository
            .findByUuid(modifiableUnitDto.getPropertyId())
            .orElseThrow(
                () -> NotFoundException.ofPropertyNotFound(modifiableUnitDto.getPropertyId()));

    final Unit unit = unitMapper.mapToUnit(modifiableUnitDto);
    unit.setProperty(property);
    property.getUnits().add(unit);

    return ResponseEntity.status(HttpStatus.CREATED).body(unitRepository.save(unit).getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> changeUnit(
      final UUID unitId, final ModifiableUnitDto modifiableUnitDto) {
    final Unit unit =
        unitRepository
            .findByUuid(unitId)
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId));

    unitMapper.updateUnit(modifiableUnitDto, unit);
    unitRepository.save(unit);
    return ResponseEntity.ok(unit.getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteUnit(final UUID unitId) {
    final Unit unit =
        unitRepository
            .findByUuid(unitId)
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId));

    if (!unit.getOwnerships().isEmpty()) {
      throw ValidationException.ofUnitHasOwnership(unit);
    }

    if (!unit.getInvoices().isEmpty()) {
      throw ValidationException.ofUnitHasInvoice(unit);
    }

    unitRepository.delete(unit);
    return ResponseEntity.noContent().<Void>build();
  }
}
