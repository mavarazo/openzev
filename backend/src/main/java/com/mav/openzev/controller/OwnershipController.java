package com.mav.openzev.controller;

import com.mav.openzev.api.OwnershipApi;
import com.mav.openzev.api.model.ModifiableOwnershipDto;
import com.mav.openzev.api.model.OwnershipDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.OwnershipMapper;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.model.Unit;
import com.mav.openzev.model.User;
import com.mav.openzev.repository.OwnershipRepository;
import com.mav.openzev.repository.UnitRepository;
import com.mav.openzev.repository.UserRepository;
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
public class OwnershipController implements OwnershipApi {

  private final UnitRepository unitRepository;
  private final UserRepository userRepository;
  private final OwnershipRepository ownershipRepository;

  private final OwnershipMapper ownershipMapper;

  @Override
  public ResponseEntity<List<OwnershipDto>> getOwnerships(final UUID unitId) {
    final Unit unit =
        unitRepository
            .findByUuid(unitId)
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId));

    return ResponseEntity.ok(
        ownershipRepository
            .findAllByUnit(unit, Sort.sort(Ownership.class).by(Ownership::getPeriodFrom))
            .stream()
            .map(ownershipMapper::mapToOwnershipDto)
            .toList());
  }

  @Override
  public ResponseEntity<OwnershipDto> getOwnership(final UUID ownershipId) {
    return ResponseEntity.ok(
        ownershipRepository
            .findByUuid(ownershipId)
            .map(ownershipMapper::mapToOwnershipDto)
            .orElseThrow(() -> NotFoundException.ofOwnershipNotFound(ownershipId)));
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createOwnership(
      final UUID unitId, final ModifiableOwnershipDto modifiableOwnershipDto) {
    final Unit unit =
        unitRepository
            .findByUuid(unitId)
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId));
    final User user =
        userRepository
            .findByUuid(modifiableOwnershipDto.getUser())
            .orElseThrow(() -> NotFoundException.ofUserNotFound(modifiableOwnershipDto.getUser()));

    final Ownership ownership = ownershipMapper.mapToOwnership(modifiableOwnershipDto);

    ownership.setUnit(unit);
    unit.getOwnerships().add(ownership);

    ownership.setUser(user);
    user.getOwnerships().add(ownership);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ownershipRepository.save(ownership).getUuid());
  }

  @Override
  public ResponseEntity<UUID> changeOwnership(
      final UUID ownershipId, final ModifiableOwnershipDto modifiableOwnershipDto) {
    final Ownership ownership =
        ownershipRepository
            .findByUuid(ownershipId)
            .orElseThrow(() -> NotFoundException.ofOwnershipNotFound(ownershipId));

    ownershipMapper.updateOwnership(modifiableOwnershipDto, ownership);
    ownershipRepository.save(ownership);
    return ResponseEntity.ok(ownership.getUuid());
  }
}
