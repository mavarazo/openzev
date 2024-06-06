package com.mav.openzev.controller;

import com.mav.openzev.api.UnitApi;
import com.mav.openzev.api.model.ModifiableUnitDto;
import com.mav.openzev.api.model.UnitDto;
import com.mav.openzev.mapper.UnitMapper;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.UnitRepository;
import com.mav.openzev.service.UnitService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UnitController implements UnitApi {

  private final UnitRepository unitRepository;
  private final UnitService unitService;
  private final UnitMapper unitMapper;

  @Override
  public ResponseEntity<List<UnitDto>> getUnits() {
    return ResponseEntity.ok(
        unitRepository.findAll(Sort.sort(Owner.class).by(Owner::getUuid)).stream()
            .map(unitMapper::mapToUnitDto)
            .toList());
  }

  @Override
  public ResponseEntity<UnitDto> getUnit(final UUID unitId) {
    final Unit unit = unitService.findUnitOrFail(unitId);
    return ResponseEntity.ok(unitMapper.mapToUnitDto(unit));
  }

  @Override
  public ResponseEntity<UUID> createUnit(final ModifiableUnitDto modifiableUnitDto) {
    final Unit unit = unitMapper.mapToUnit(modifiableUnitDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(unitRepository.save(unit).getUuid());
  }

  @Override
  public ResponseEntity<UUID> changeUnit(
      final UUID unitId, final ModifiableUnitDto modifiableUnitDto) {
    final Unit unit = unitService.findUnitOrFail(unitId);
    unitMapper.updateUnit(modifiableUnitDto, unit);
    unitRepository.save(unit);
    return ResponseEntity.ok(unit.getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteUnit(final UUID unitId) {
    unitService.deleteUnit(unitId);
    return ResponseEntity.noContent().<Void>build();
  }
}
