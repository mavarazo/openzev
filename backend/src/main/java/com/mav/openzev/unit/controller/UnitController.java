package com.mav.openzev.unit.controller;

import com.mav.openzev.api.UnitApi;
import com.mav.openzev.api.model.UnitDto;
import com.mav.openzev.unit.entity.Unit;
import com.mav.openzev.unit.mapper.UnitMapper;
import com.mav.openzev.unit.model.ChangeUnitCommand;
import com.mav.openzev.unit.model.CreateUnitCommand;
import com.mav.openzev.unit.service.UnitService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UnitController implements UnitApi {

  private final UnitService unitService;
  private final UnitMapper unitMapper;

  @Override
  public ResponseEntity<List<UnitDto>> getUnits() {
    final List<UnitDto> unitDtos =
        unitService.getUnits().stream().map(unitMapper::mapToUnitDto).toList();
    return ResponseEntity.ok(unitDtos);
  }

  @Override
  public ResponseEntity<UnitDto> createUnit(final UnitDto unitDto) {
    final CreateUnitCommand command = unitMapper.mapToCreateUnitCommand(unitDto);
    final Unit unit = unitService.createUnit(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(unitMapper.mapToUnitDto(unit));
  }

  @Override
  public ResponseEntity<UnitDto> getUnit(final UUID unitId) {
    final Unit unit = unitService.getUnit(unitId);
    return ResponseEntity.ok(unitMapper.mapToUnitDto(unit));
  }

  @Override
  public ResponseEntity<UnitDto> changeUnit(final UUID unitId, final UnitDto unitDto) {
    final ChangeUnitCommand command = unitMapper.mapToChangeUnitCommand(unitId, unitDto);
    final Unit unit = unitService.changeUnit(command);
    return ResponseEntity.ok(unitMapper.mapToUnitDto(unit));
  }

  @Override
  public ResponseEntity<Void> deleteUnit(final UUID unitId) {
    unitService.deleteUnit(unitId);
    return ResponseEntity.noContent().build();
  }
}
