package com.mav.openzev.controller;

import com.mav.openzev.api.OwnerApi;
import com.mav.openzev.api.model.ModifiableOwnerDto;
import com.mav.openzev.api.model.OwnerDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.OwnerMapper;
import com.mav.openzev.model.Owner;
import com.mav.openzev.repository.OwnerRepository;
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
public class OwnerController implements OwnerApi {

  private final OwnerRepository ownerRepository;

  private final OwnerMapper ownerMapper;

  @Override
  public ResponseEntity<List<OwnerDto>> getOwners() {
    return ResponseEntity.ok(
        ownerRepository.findAll(Sort.sort(Owner.class).by(Owner::getUuid)).stream()
            .map(ownerMapper::mapToOwnerDto)
            .toList());
  }

  @Override
  public ResponseEntity<OwnerDto> getOwner(final UUID ownerId) {
    return ResponseEntity.ok(
        ownerRepository
            .findByUuid(ownerId)
            .map(ownerMapper::mapToOwnerDto)
            .orElseThrow(() -> NotFoundException.ofOwnerNotFound(ownerId)));
  }

  @Override
  public ResponseEntity<UUID> createOwner(final ModifiableOwnerDto modifiableOwnerDto) {
    final Owner owner = ownerRepository.save(ownerMapper.mapToOwner(modifiableOwnerDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(owner.getUuid());
  }

  @Override
  public ResponseEntity<UUID> changeOwner(
      final UUID ownerId, final ModifiableOwnerDto modifiableOwnerDto) {
    return ownerRepository
        .findByUuid(ownerId)
        .map(
            owner -> {
              ownerMapper.updateOwner(modifiableOwnerDto, owner);
              return ResponseEntity.ok(ownerRepository.save(owner).getUuid());
            })
        .orElseThrow(() -> NotFoundException.ofOwnerNotFound(ownerId));
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteOwner(final UUID ownerId) {
    final Owner owner =
        ownerRepository
            .findByUuid(ownerId)
            .orElseThrow(() -> NotFoundException.ofOwnerNotFound(ownerId));

    if (!owner.getOwnerships().isEmpty()) {
      throw ValidationException.ofOwnerHasOwnership(owner);
    }

    ownerRepository.delete(owner);
    return ResponseEntity.noContent().<Void>build();
  }
}
