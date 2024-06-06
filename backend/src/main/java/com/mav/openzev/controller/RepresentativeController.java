package com.mav.openzev.controller;

import com.mav.openzev.api.RepresentativeApi;
import com.mav.openzev.api.model.ModifiableRepresentativeDto;
import com.mav.openzev.api.model.RepresentativeDto;
import com.mav.openzev.mapper.RepresentativeMapper;
import com.mav.openzev.model.Representative;
import com.mav.openzev.repository.RepresentativeRepository;
import com.mav.openzev.service.RepresentativeService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RepresentativeController implements RepresentativeApi {

  private final RepresentativeRepository representativeRepository;
  private final RepresentativeService representativeService;
  private final RepresentativeMapper representativeMapper;

  @Override
  public ResponseEntity<List<RepresentativeDto>> getRepresentatives() {
    return ResponseEntity.ok(
        representativeRepository
            .findAll(Sort.sort(Representative.class).by(Representative::getCreated).ascending())
            .stream()
            .map(representativeMapper::mapToRepresentativeDto)
            .toList());
  }

  @Override
  public ResponseEntity<UUID> createRepresentative(
      final ModifiableRepresentativeDto modifiableRepresentativeDto) {
    final Representative representative =
        representativeMapper.mapToRepresentative(modifiableRepresentativeDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(representativeService.saveRepresentative(representative).getUuid());
  }

  @Override
  public ResponseEntity<RepresentativeDto> getRepresentative(final UUID representativeId) {
    final Representative representative =
        representativeService.findRepresentativeOrFail(representativeId);
    return ResponseEntity.ok(representativeMapper.mapToRepresentativeDto(representative));
  }

  @Override
  public ResponseEntity<UUID> changeRepresentative(
      final UUID representativeId, final ModifiableRepresentativeDto modifiableRepresentativeDto) {
    final Representative representative =
        representativeService.findRepresentativeOrFail(representativeId);

    representativeMapper.updateRepresentative(modifiableRepresentativeDto, representative);
    return ResponseEntity.ok(representativeService.saveRepresentative(representative).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteRepresentative(final UUID representativeId) {
    final Representative representative =
        representativeService.findRepresentativeOrFail(representativeId);
    representativeRepository.delete(representative);
    return ResponseEntity.noContent().<Void>build();
  }
}
