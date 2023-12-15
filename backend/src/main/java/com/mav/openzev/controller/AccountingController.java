package com.mav.openzev.controller;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.mav.openzev.api.AccountingApi;
import com.mav.openzev.api.model.AnyAccounting;
import com.mav.openzev.api.model.AnyModifiableAccounting;
import com.mav.openzev.api.model.DocumentDto;
import com.mav.openzev.api.model.ModifiableAccountingDto;
import com.mav.openzev.api.model.ModifiableZevAccountingDto;
import com.mav.openzev.exception.BadRequestException;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.AccountingMapper;
import com.mav.openzev.mapper.DocumentMapper;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.model.Document;
import com.mav.openzev.model.zev.ZevAccounting;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.AgreementRepository;
import com.mav.openzev.repository.DocumentRepository;
import com.mav.openzev.service.DocumentService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AccountingController implements AccountingApi {

  private final AccountingRepository accountingRepository;
  private final AgreementRepository agreementRepository;
  private final DocumentRepository documentRepository;

  private final AccountingMapper accountingMapper;
  private final DocumentMapper documentMapper;

  private final DocumentService documentService;

  @Override
  @Transactional
  public ResponseEntity<List<AnyAccounting>> getAccountings() {
    return ResponseEntity.ok(
        accountingRepository
            .findAll(Sort.sort(Accounting.class).by(Accounting::getPeriodFrom))
            .stream()
            .map(this::mapToAccounting)
            .toList());
  }

  private <T extends Accounting> AnyAccounting mapToAccounting(final T accounting) {
    if (nonNull(accounting)) {
      return accountingMapper.mapToAccountingDto(accounting);
    }
    return null;
  }

  @Override
  @Transactional
  public ResponseEntity<AnyAccounting> getAccounting(final UUID accountingId) {
    final Optional<Accounting> optionalAccounting = accountingRepository.findByUuid(accountingId);

    if (optionalAccounting.isEmpty()) {
      throw NotFoundException.ofAccountingNotFound(accountingId);
    }

    final AnyAccounting anyAccounting =
        switch (optionalAccounting.get()) {
          case final ZevAccounting zevAccounting -> accountingMapper.mapToAccountingDto(
              zevAccounting);
          case final Accounting accounting -> accountingMapper.mapToAccountingDto(accounting);
        };

    return ResponseEntity.ok(anyAccounting);
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createAccounting(
      final AnyModifiableAccounting anyModifiableAccounting) {

    final Accounting accounting =
        switch (anyModifiableAccounting) {
          case final ModifiableAccountingDto modifiableAccountingDto -> saveAccounting(
              modifiableAccountingDto);
          case final ModifiableZevAccountingDto modifiableZevAccountingDto -> saveAccounting(
              modifiableZevAccountingDto);
          default -> throw new IllegalStateException(
              "Unexpected value: " + anyModifiableAccounting);
        };

    return ResponseEntity.status(HttpStatus.CREATED).body(accounting.getUuid());
  }

  private Accounting saveAccounting(final ModifiableAccountingDto modifiableAccountingDto) {
    final Accounting accounting = accountingMapper.mapToAccounting(modifiableAccountingDto);
    return accountingRepository.save(accounting);
  }

  private ZevAccounting saveAccounting(
      final ModifiableZevAccountingDto modifiableZevAccountingDto) {
    final ZevAccounting accounting = accountingMapper.mapToAccounting(modifiableZevAccountingDto);
    accounting.setAgreement(getAgreement(modifiableZevAccountingDto.getAgreementId()));
    return accountingRepository.save(accounting);
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> changeAccounting(
      final UUID accountingId, final AnyModifiableAccounting anyModifiableAccounting) {

    final Accounting accounting =
        accountingRepository
            .findByUuid(accountingId)
            .orElseThrow(() -> NotFoundException.ofAccountingNotFound(accountingId));

    if (anyModifiableAccounting
            instanceof final ModifiableZevAccountingDto modifiableZevAccountingDto
        && accounting instanceof final ZevAccounting zevAccounting) {
      accountingMapper.updateAccounting(modifiableZevAccountingDto, zevAccounting);
      zevAccounting.setAgreement(getAgreement(modifiableZevAccountingDto.getAgreementId()));
    } else if (anyModifiableAccounting
        instanceof final ModifiableAccountingDto modifiableAccountingDto) {
      accountingMapper.updateAccounting(modifiableAccountingDto, accounting);
    }

    return ResponseEntity.ok(accountingRepository.save(accounting).getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteAccounting(final UUID accountingId) {
    final Accounting accounting =
        accountingRepository
            .findByUuid(accountingId)
            .orElseThrow(() -> NotFoundException.ofAccountingNotFound(accountingId));

    if (!accounting.getInvoices().isEmpty()) {
      throw ValidationException.ofAccountingHasInvoice(accounting);
    }

    accountingRepository.delete(accounting);
    return ResponseEntity.noContent().build();
  }

  private Agreement getAgreement(final UUID agreementId) {
    if (isNull(agreementId)) {
      return null;
    }

    return agreementRepository
        .findByUuid(agreementId)
        .orElseThrow(() -> NotFoundException.ofAgreementNotFound(agreementId));
  }

  @Override
  public ResponseEntity<List<DocumentDto>> getAccountingDocuments(final UUID accountingId) {
    final Accounting accounting =
        accountingRepository
            .findByUuid(accountingId)
            .orElseThrow(() -> NotFoundException.ofAccountingNotFound(accountingId));

    final List<DocumentDto> documents =
        documentRepository
            .findAllByRefIdAndRefType(
                accounting.getId(),
                Accounting.class.getSimpleName(),
                Sort.sort(Document.class).by(Document::getCreated))
            .stream()
            .map(documentMapper::mapToDocumentDto)
            .toList();

    return ResponseEntity.ok(documents);
  }

  @SneakyThrows
  @Override
  public ResponseEntity<UUID> createAccountingDocument(
      final UUID accountingId, final MultipartFile content) {
    if (isNull(content) || isNull(content.getContentType())) {
      throw BadRequestException.ofMultipartFileRequired();
    }

    final Accounting accounting =
        accountingRepository
            .findByUuid(accountingId)
            .orElseThrow(() -> NotFoundException.ofAccountingNotFound(accountingId));

    final Document document = documentService.createDocument(content, accounting);
    return ResponseEntity.status(HttpStatus.CREATED).body(document.getUuid());
  }
}
