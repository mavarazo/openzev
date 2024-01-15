package com.mav.openzev.controller;

import static java.util.Objects.isNull;

import com.mav.openzev.api.InvoiceApi;
import com.mav.openzev.api.model.InvoiceDto;
import com.mav.openzev.api.model.ModifiableInvoiceDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.InvoiceMapper;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Owner;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.repository.OwnerRepository;
import com.mav.openzev.repository.UnitRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InvoiceController implements InvoiceApi {

  private final InvoiceRepository invoiceRepository;
  private final UnitRepository unitRepository;
  private final OwnerRepository ownerRepository;

  private final InvoiceMapper invoiceMapper;

  @Override
  public ResponseEntity<List<InvoiceDto>> getInvoices() {
    return ResponseEntity.ok(
        invoiceRepository.findAll(Sort.sort(Invoice.class).by(Invoice::getCreated)).stream()
            .map(invoiceMapper::mapToInvoiceDto)
            .toList());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createInvoice(final ModifiableInvoiceDto modifiableInvoiceDto) {
    final Invoice invoice = invoiceMapper.mapToInvoice(modifiableInvoiceDto);
    findUnit(modifiableInvoiceDto.getUnitId()).ifPresent(invoice::setUnit);
    invoice.setRecipient(findRecipientOrFail(modifiableInvoiceDto.getRecipientId()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  public ResponseEntity<InvoiceDto> getInvoice(final UUID invoiceId) {
    final Invoice invoice = findInvoiceOrFail(invoiceId);
    return ResponseEntity.ok(invoiceMapper.mapToInvoiceDto(invoice));
  }

  @Override
  public ResponseEntity<UUID> changeInvoice(
      final UUID invoiceId, final ModifiableInvoiceDto modifiableInvoiceDto) {
    final Invoice invoice = findInvoiceOrFail(invoiceId);
    invoiceMapper.updateInvoice(modifiableInvoiceDto, invoice);
    findUnit(modifiableInvoiceDto.getUnitId()).ifPresent(invoice::setUnit);
    invoice.setRecipient(findRecipientOrFail(modifiableInvoiceDto.getRecipientId()));
    return ResponseEntity.ok(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteInvoice(final UUID invoiceId) {
    invoiceRepository.delete(findInvoiceOrFail(invoiceId));
    return ResponseEntity.noContent().build();
  }

  private Invoice findInvoiceOrFail(final UUID invoiceId) {
    return invoiceRepository
        .findByUuid(invoiceId)
        .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));
  }

  private Optional<Unit> findUnit(final UUID unitId) {
    return isNull(unitId)
        ? Optional.empty()
        : Optional.ofNullable(
            unitRepository
                .findByUuid(unitId)
                .orElseThrow(() -> NotFoundException.ofUnitNotFound(unitId)));
  }

  private Owner findRecipientOrFail(final UUID recipientId) {
    return ownerRepository
        .findByUuid(recipientId)
        .orElseThrow(() -> NotFoundException.ofOwnerNotFound(recipientId));
  }
}
