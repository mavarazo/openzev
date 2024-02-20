package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Ownership;
import com.mav.openzev.repository.OwnershipRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnershipService {

  private final OwnershipRepository ownershipRepository;

  public Ownership findOwnershipOrFail(final UUID ownershipId) {
    return ownershipRepository
        .findByUuid(ownershipId)
        .orElseThrow(() -> NotFoundException.ofOwnershipNotFound(ownershipId));
  }
}
