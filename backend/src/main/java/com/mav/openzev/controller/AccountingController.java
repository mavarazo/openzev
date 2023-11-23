package com.mav.openzev.controller;

import static java.util.Objects.isNull;

import com.mav.openzev.api.AccountingApi;
import com.mav.openzev.api.model.AccountingDto;
import com.mav.openzev.api.model.DocumentDto;
import com.mav.openzev.api.model.ModifiableAccountingDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.mapper.AccountingMapper;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Agreement;
import com.mav.openzev.model.Document;
import com.mav.openzev.model.Property;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.AgreementRepository;
import com.mav.openzev.repository.DocumentRepository;
import com.mav.openzev.repository.PropertyRepository;
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
  private final PropertyRepository propertyRepository;
  private final AgreementRepository agreementRepository;
  private final DocumentRepository documentRepository;

  private final AccountingMapper accountingMapper;

  @Override
  @Transactional
  public ResponseEntity<List<AccountingDto>> getAccountings(final UUID propertyId) {
    return ResponseEntity.ok(
        accountingRepository
            .findByProperty_Uuid(
                propertyId, Sort.sort(Accounting.class).by(Accounting::getPeriodFrom))
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
      final UUID propertyId, final ModifiableAccountingDto modifiableAccountingDto) {
    final Property property =
        propertyRepository
            .findByUuid(propertyId)
            .orElseThrow(() -> NotFoundException.ofPropertyNotFound(propertyId));

    final Accounting accounting = accountingMapper.mapToAccounting(modifiableAccountingDto);
    property.addAccounting(accounting);
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
            .map(
                d ->
                    new DocumentDto()
                        .id(d.getUuid())
                        .name(d.getName())
                        .filename(d.getFilename())
                        .mimeType(d.getMimeType()))
            .toList();

    return ResponseEntity.ok(documents);
  }

  @SneakyThrows
  @Override
  public ResponseEntity<UUID> createAccountingDocument(
      final UUID accountingId, final MultipartFile content) {
    final Accounting accounting =
        accountingRepository
            .findByUuid(accountingId)
            .orElseThrow(() -> NotFoundException.ofAccountingNotFound(accountingId));

    final Document document = new Document();
    document.setRefId(accounting.getId());
    document.setRefType(Accounting.class.getSimpleName());
    document.setName(content.getName());
    document.setFilename(content.getOriginalFilename());
    document.setMimeType(content.getContentType());
    document.setData(content.getBytes());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(documentRepository.save(document).getUuid());
  }
}
