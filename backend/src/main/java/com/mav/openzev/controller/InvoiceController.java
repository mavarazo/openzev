package com.mav.openzev.controller;


import com.mav.openzev.api.InvoiceApi;
import com.mav.openzev.api.model.InvoiceDto;
import com.mav.openzev.api.model.ModifiableInvoiceDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.InvoiceMapper;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.repository.UnitRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InvoiceController implements InvoiceApi {

  private final AccountingRepository accountingRepository;
  private final InvoiceRepository invoiceRepository;
  private final UnitRepository unitRepository;

  private final InvoiceMapper invoiceMapper;

  @Override
  public ResponseEntity<List<InvoiceDto>> getInvoices(final UUID accountingId) {
    return ResponseEntity.ok(
        invoiceRepository
            .findAllByAccounting_Uuid(
                accountingId, Sort.sort(Invoice.class).by(Invoice::getCreated))
            .stream()
            .map(invoiceMapper::mapToInvoiceDto)
            .toList());
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> createInvoice(
      final UUID accountingId, final ModifiableInvoiceDto modifiableInvoiceDto) {
    final Accounting accounting =
        accountingRepository
            .findByUuid(accountingId)
            .orElseThrow(() -> NotFoundException.ofAccountingNotFound(accountingId));

    final Unit unit =
        unitRepository
            .findByUuid(modifiableInvoiceDto.getUnitId())
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(modifiableInvoiceDto.getUnitId()));

    final Invoice invoice = invoiceMapper.mapToInvoice(modifiableInvoiceDto);
    invoice.setAccounting(accounting);
    accounting.getInvoices().add(invoice);
    invoice.setUnit(unit);
    unit.getInvoices().add(invoice);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  public ResponseEntity<InvoiceDto> getInvoice(final UUID invoiceId) {
    return ResponseEntity.ok(
        invoiceRepository
            .findByUuid(invoiceId)
            .map(invoiceMapper::mapToInvoiceDto)
            .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId)));
  }

  @Override
  public ResponseEntity<UUID> changeInvoice(
      final UUID invoiceId, final ModifiableInvoiceDto modifiableInvoiceDto) {
    final Invoice invoice =
        invoiceRepository
            .findByUuid(invoiceId)
            .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));

    invoiceMapper.updateInvoice(modifiableInvoiceDto, invoice);
    return ResponseEntity.ok(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  @Transactional
  public ResponseEntity<Void> deleteInvoice(final UUID invoiceId) {
    return invoiceRepository
        .findByUuid(invoiceId)
        .map(
            invoice -> {
              invoiceRepository.delete(invoice);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));
  }
}
