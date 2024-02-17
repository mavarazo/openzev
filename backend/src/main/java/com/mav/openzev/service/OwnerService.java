package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Owner;
import com.mav.openzev.repository.OwnerRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {

  private final OwnerRepository ownerRepository;

  public Owner findOwnerOrFail(final UUID ownerId) {
    return ownerRepository
        .findByUuid(ownerId)
        .orElseThrow(() -> NotFoundException.ofOwnerNotFound(ownerId));
  }
}
