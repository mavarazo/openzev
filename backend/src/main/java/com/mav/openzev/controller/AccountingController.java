package com.mav.openzev.controller;


import com.mav.openzev.api.AccountingApi;
import com.mav.openzev.api.model.AccountingDto;
import com.mav.openzev.api.model.ModifiableAccountingDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.AccountingMapper;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.AgreementRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountingController implements AccountingApi {

  private final AccountingRepository accountingRepository;
  private final AgreementRepository agreementRepository;

  private final AccountingMapper accountingMapper;

  @Override
  @Transactional
  public ResponseEntity<List<AccountingDto>> getAccountings() {
    final List<AccountingDto> result = new ArrayList<>();

    accountingRepository.findAll(Sort.sort(Accounting.class).by(Accounting::getPeriodFrom)).stream()
        .map(accountingMapper::mapToAccountingDto)
        .forEach(result::add);

    return ResponseEntity.ok(result);
  }

  @Override
  @Transactional
  public ResponseEntity<AccountingDto> getAccounting(final UUID accountingId) {
    return ResponseEntity.ok(
        accountingRepository
            .findByUuid(accountingId)
            .map(accountingMapper::mapToAccountingDto)
            .orElseThrow(() -> NotFoundException.ofAccountingNotFound(accountingId)));
  }

  @Override
  public ResponseEntity<UUID> createAccounting(
      final ModifiableAccountingDto modifiableAccountingDto) {
    final Accounting accounting = accountingMapper.mapToAccounting(modifiableAccountingDto);
    accounting.setAgreement(getAgreement(modifiableAccountingDto.getAgreement()));

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(accountingRepository.save(accounting).getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> changeAccounting(
      final UUID accountingId, final ModifiableAccountingDto modifiableAccountingDto) {
    final Accounting accounting =
        accountingRepository
            .findByUuid(accountingId)
            .orElseThrow(() -> NotFoundException.ofAccountingNotFound(accountingId));
    accountingMapper.updateAccounting(modifiableAccountingDto, accounting);
    accounting.setAgreement(getAgreement(modifiableAccountingDto.getAgreement()));

    return ResponseEntity.ok(accountingRepository.save(accounting).getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteAccounting(final UUID accountingId) {
    final Accounting accounting =
        accountingRepository
            .findByUuid(accountingId)
            .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(accountingId));

    if (!accounting.getInvoices().isEmpty()) {
      throw ValidationException.ofAccountingHasInvoice(accounting);
    }

    accountingRepository.delete(accounting);
    return ResponseEntity.noContent().<Void>build();
  }

  private Agreement getAgreement(final UUID agreementId) {
    if (Objects.isNull(agreementId)) {
      return null;
    }

    return agreementRepository
        .findByUuid(agreementId)
        .orElseThrow(() -> NotFoundException.ofAgreementNotFound(agreementId));
  }
}
