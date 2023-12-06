package com.mav.openzev.controller;

import static java.util.Objects.isNull;

import com.mav.openzev.api.AccountingApi;
import com.mav.openzev.api.model.AccountingDto;
import com.mav.openzev.api.model.DocumentDto;
import com.mav.openzev.api.model.ModifiableAccountingDto;
import com.mav.openzev.exception.BadRequestException;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.AccountingMapper;
import com.mav.openzev.mapper.DocumentMapper;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.model.Document;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.AgreementRepository;
import com.mav.openzev.repository.DocumentRepository;
import com.mav.openzev.service.DocumentService;
import java.util.List;
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
  public ResponseEntity<List<AccountingDto>> getAccountings() {
    return ResponseEntity.ok(
        accountingRepository
            .findAll(Sort.sort(Accounting.class).by(Accounting::getPeriodFrom))
            .stream()
            .map(accountingMapper::mapToAccountingDto)
            .toList());
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
  @Transactional
  public ResponseEntity<UUID> createAccounting(
      final ModifiableAccountingDto modifiableAccountingDto) {
    final Accounting accounting = accountingMapper.mapToAccounting(modifiableAccountingDto);
    accounting.setAgreement(getAgreement(modifiableAccountingDto.getAgreementId()));

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
    accounting.setAgreement(getAgreement(modifiableAccountingDto.getAgreementId()));

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
