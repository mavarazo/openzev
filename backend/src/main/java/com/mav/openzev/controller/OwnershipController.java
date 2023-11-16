package com.mav.openzev.controller;

import static java.util.Objects.isNull;

import com.mav.openzev.api.OwnershipApi;
import com.mav.openzev.api.model.ModifiableOwnershipDto;
import com.mav.openzev.api.model.OwnershipDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.OwnershipMapper;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.OwnerRepository;
import com.mav.openzev.repository.OwnershipRepository;
import com.mav.openzev.repository.UnitRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

  private final OwnershipMapper ownershipMapper;

  @Override
  public ResponseEntity<List<OwnershipDto>> getOwnerships(
      final UUID unitId, final LocalDate validFrom, final LocalDate validUpto) {
    final Unit unit =
        unitRepository
            .findByUuid(unitId)
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId));

    final List<OwnershipDto> result =
        ownershipRepository
            .findAllByUnit(unit, Sort.sort(Ownership.class).by(Ownership::getPeriodFrom))
            .stream()
            .filter(
                o ->
                    isNull(validFrom)
                        || isNull(o.getPeriodUpto())
                        || validFrom.isBefore(o.getPeriodUpto()))
            .filter(o -> isNull(validUpto) || validUpto.isAfter(o.getPeriodFrom()))
            .map(ownershipMapper::mapToOwnershipDto)
            .toList();

    return ResponseEntity.ok(result);
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
  public ResponseEntity<UUID> createOwnership(final ModifiableOwnershipDto modifiableOwnershipDto) {
    final Unit unit =
        unitRepository
            .findByUuid(modifiableOwnershipDto.getUnitId())
            .orElseThrow(
                () -> NotFoundException.ofUnitNotFound(modifiableOwnershipDto.getUnitId()));
    final Owner owner =
        ownerRepository
            .findByUuid(modifiableOwnershipDto.getOwnerId())
            .orElseThrow(
                () -> NotFoundException.ofOwnerNotFound(modifiableOwnershipDto.getOwnerId()));

    final Ownership ownership = ownershipMapper.mapToOwnership(modifiableOwnershipDto);

    assertNoOverlap(unit, ownership);
    ownership.setUnit(unit);
    unit.getOwnerships().add(ownership);

    ownership.setOwner(owner);
    owner.getOwnerships().add(ownership);

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

    assertNoOverlap(ownership.getUnit(), ownership);

    ownershipRepository.save(ownership);
    return ResponseEntity.ok(ownership.getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteOwnership(final UUID ownershipId) {
    return ownershipRepository
        .findByUuid(ownershipId)
        .map(
            ownership -> {
              ownershipRepository.delete(ownership);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseThrow(() -> NotFoundException.ofOwnershipNotFound(ownershipId));
  }

  private void assertNoOverlap(final Unit unit, final Ownership ownership) {
    final Optional<Ownership> optionalOverlap =
        ownershipRepository
            .findAllByUnit(unit, Sort.sort(Ownership.class).by(Ownership::getPeriodFrom))
            .stream()
            .filter(o -> !o.getUuid().equals(ownership.getUuid()))
            .filter(
                o -> {
                  if (isNull(o.getPeriodUpto())) {
                    return true;
                  }
                  return !ownership.getPeriodFrom().isAfter(o.getPeriodUpto());
                })
            .findFirst();

    if (optionalOverlap.isPresent()) {
      throw ValidationException.ofOwnershipOverlap(ownership, optionalOverlap.get());
    }
  }
}
