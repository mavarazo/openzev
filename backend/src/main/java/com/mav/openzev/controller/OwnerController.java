package com.mav.openzev.controller;

import com.mav.openzev.api.OwnerApi;
import com.mav.openzev.api.model.ModifiableOwnerDto;
import com.mav.openzev.api.model.OwnerDto;
import com.mav.openzev.mapper.OwnerMapper;
import com.mav.openzev.model.Owner;
import com.mav.openzev.repository.OwnerRepository;
import com.mav.openzev.service.OwnerService;
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
  private final OwnerService ownerService;
  private final OwnerMapper ownerMapper;

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<List<OwnerDto>> getOwners() {
    final Sort sortByFirstName = Sort.sort(Owner.class).by(Owner::getFirstName).ascending();
    final Sort sortByLastName = Sort.sort(Owner.class).by(Owner::getLastName).ascending();

    return ResponseEntity.ok(
        ownerRepository.findAll(sortByFirstName.and(sortByLastName)).stream()
            .map(ownerMapper::mapToOwnerDto)
            .toList());
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<OwnerDto> getOwner(final UUID ownerId) {
    final Owner owner = ownerService.findOwnerOrFail(ownerId);
    return ResponseEntity.ok(ownerMapper.mapToOwnerDto(owner));
  }

  @Override
  public ResponseEntity<UUID> createOwner(final ModifiableOwnerDto modifiableOwnerDto) {
    final Owner owner = ownerMapper.mapToOwner(modifiableOwnerDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(ownerRepository.save(owner).getUuid());
  }

  @Override
  public ResponseEntity<UUID> changeOwner(
      final UUID ownerId, final ModifiableOwnerDto modifiableOwnerDto) {
    final Owner owner = ownerService.findOwnerOrFail(ownerId);
    ownerMapper.updateOwner(modifiableOwnerDto, owner);
    return ResponseEntity.ok(ownerRepository.save(owner).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteOwner(final UUID ownerId) {
    ownerService.deleteOwner(ownerId);
    return ResponseEntity.noContent().build();
  }
}
