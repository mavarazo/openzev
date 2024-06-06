package com.mav.openzev.controller;


import com.mav.openzev.api.OwnershipApi;
import com.mav.openzev.api.model.ModifiableOwnershipDto;
import com.mav.openzev.api.model.OwnershipDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.OwnershipMapper;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.OwnerRepository;
import com.mav.openzev.repository.OwnershipRepository;
import com.mav.openzev.repository.UnitRepository;
import com.mav.openzev.service.OwnershipService;
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
  private final OwnerRepository ownerRepository;
  private final OwnershipRepository ownershipRepository;
  private final OwnershipService ownershipService;

  private final OwnershipMapper ownershipMapper;

  @Override
  public ResponseEntity<List<OwnershipDto>> getOwnerships(final UUID unitId) {
    final List<OwnershipDto> result =
        ownershipRepository
            .findByUnit_Uuid(
                unitId, Sort.sort(Ownership.class).by(Ownership::getCreated).descending())
            .stream()
            .map(ownershipMapper::mapToOwnershipDto)
            .toList();

    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<OwnershipDto> getOwnership(final UUID ownershipId) {
    final Ownership ownership = ownershipService.findOwnershipOrFail(ownershipId);
    return ResponseEntity.ok(ownershipMapper.mapToOwnershipDto(ownership));
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createOwnership(
      final UUID unitId, final ModifiableOwnershipDto modifiableOwnershipDto) {
    final Unit unit =
        unitRepository
            .findByUuid(unitId)
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId));

    final Owner owner =
        ownerRepository
            .findByUuid(modifiableOwnershipDto.getOwnerId())
            .orElseThrow(
                () -> NotFoundException.ofOwnerNotFound(modifiableOwnershipDto.getOwnerId()));

    final Ownership ownership = ownershipMapper.mapToOwnership(modifiableOwnershipDto);
    unit.addOwnership(ownership);
    owner.addOwnership(ownership);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ownershipRepository.save(ownership).getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> changeOwnership(
      final UUID ownershipId, final ModifiableOwnershipDto modifiableOwnershipDto) {
    final Ownership ownership = ownershipService.findOwnershipOrFail(ownershipId);
    ownershipMapper.updateOwnership(modifiableOwnershipDto, ownership);
    ownershipRepository.save(ownership);
    return ResponseEntity.ok(ownership.getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteOwnership(final UUID ownershipId) {
    final Ownership ownership = ownershipService.findOwnershipOrFail(ownershipId);
    ownershipRepository.delete(ownership);
    return ResponseEntity.noContent().build();
  }
}
