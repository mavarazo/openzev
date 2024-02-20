package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Representative;
import com.mav.openzev.repository.RepresentativeRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepresentativeService {

  private final RepresentativeRepository representativeRepository;

  public Representative findRepresentativeOrFail(final UUID representativeId) {
    return representativeRepository
        .findByUuid(representativeId)
        .orElseThrow(() -> NotFoundException.ofRepresentativeNotFound(representativeId));
  }

  @Transactional
  public Representative saveRepresentative(final Representative representative) {
    if (representative.isActive()) {
      representativeRepository.findAll().stream()
          .filter(r -> !r.getUuid().equals(representative.getUuid()))
          .forEach(b -> b.setActive(false));
    }
    return representativeRepository.save(representative);
  }
}
