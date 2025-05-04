package com.mav.openzev.unit.service;

import com.mav.openzev.unit.entity.Unit;
import com.mav.openzev.unit.entity.Unit_;
import com.mav.openzev.unit.model.ChangeUnitCommand;
import com.mav.openzev.unit.model.CreateUnitCommand;
import com.mav.openzev.unit.repository.UnitRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitService {

  private final UnitRepository unitRepository;

  public List<Unit> getUnits() {
    return unitRepository.findAll(Sort.by(Unit_.NUMBER).ascending());
  }

  public Unit getUnit(final UUID id) {
    return unitRepository.findByIdOrFail(id);
  }

  public Unit createUnit(final CreateUnitCommand command) {
    return unitRepository.save(
        Unit.builder()
            .number(command.number())
            .firstName(command.firstName())
            .lastName(command.lastName())
            .build());
  }

  public Unit changeUnit(final ChangeUnitCommand command) {
    final Unit unit = unitRepository.findByIdOrFail(command.id());
    unit.setNumber(command.number());
    unit.setFirstName(command.firstName());
    unit.setLastName(command.lastName());
    return unitRepository.save(unit);
  }

  public void deleteUnit(final UUID id) {
    final Unit unit = unitRepository.findByIdOrFail(id);
    unitRepository.delete(unit);
  }
}
