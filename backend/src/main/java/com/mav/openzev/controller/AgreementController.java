package com.mav.openzev.controller;

import com.mav.openzev.api.AgreementApi;
import com.mav.openzev.api.model.AgreementDto;
import com.mav.openzev.api.model.ModifiableAgreementDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.AgreementMapper;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.repository.AgreementRepository;
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
public class AgreementController implements AgreementApi {

  private final AgreementRepository agreementRepository;

  private final AgreementMapper agreementMapper;

  @Override
  public ResponseEntity<List<AgreementDto>> getAgreements() {
    return ResponseEntity.ok(
        agreementRepository
            .findAll(Sort.sort(Agreement.class).by(Agreement::getPeriodFrom))
            .stream()
            .map(agreementMapper::mapToAgreementDto)
            .toList());
  }

  @Override
  public ResponseEntity<AgreementDto> getAgreement(final UUID agreementId) {
    return ResponseEntity.ok(
        agreementRepository
            .findByUuid(agreementId)
            .map(agreementMapper::mapToAgreementDto)
            .orElseThrow(() -> NotFoundException.ofAgreementNotFound(agreementId)));
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createAgreement(final ModifiableAgreementDto modifiableAgreementDto) {
    final Agreement agreement = agreementMapper.mapToAgreement(modifiableAgreementDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(agreementRepository.save(agreement).getUuid());
  }

  @Override
  public ResponseEntity<UUID> changeAgreement(
      final UUID agreementId, final ModifiableAgreementDto modifiableAgreementDto) {
    final Agreement agreement =
        agreementRepository
            .findByUuid(agreementId)
            .orElseThrow(() -> NotFoundException.ofAgreementNotFound(agreementId));

    agreementMapper.updateAgreement(modifiableAgreementDto, agreement);
    agreementRepository.save(agreement);
    return ResponseEntity.ok(agreement.getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteAgreement(final UUID agreementId) {
    final Agreement agreement =
        agreementRepository
            .findByUuid(agreementId)
            .orElseThrow(() -> NotFoundException.ofAgreementNotFound(agreementId));

    if (!agreement.getAccountings().isEmpty()) {
      throw ValidationException.ofAgreementHasAccounting(agreement);
    }

    agreementRepository.delete(agreement);
    return ResponseEntity.noContent().<Void>build();
  }
}
