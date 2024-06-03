package com.mav.openzev.service;

import static java.util.Objects.isNull;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.model.Owner;
import com.mav.openzev.repository.OwnerRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService {

  private final OwnerRepository ownerRepository;

  public Owner findOwnerOrFail(final UUID ownerId) {
    return ownerRepository
        .findByUuid(ownerId)
        .orElseThrow(() -> NotFoundException.ofOwnerNotFound(ownerId));
  }

  public Optional<Owner> findOwner(final UUID ownerId) {
    return isNull(ownerId) ? Optional.empty() : Optional.ofNullable(findOwnerOrFail(ownerId));
  }

  @Transactional
  public void deleteOwner(final UUID ownerId) {
    final Owner owner = findOwnerOrFail(ownerId);

    if (!owner.getOwnerships().isEmpty()) {
      throw ValidationException.ofOwnerHasOwnership(owner);
    }

    ownerRepository.delete(owner);
  }
}
