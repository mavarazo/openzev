package com.mav.openzev.controller;

import com.mav.openzev.api.InvoiceApi;
import com.mav.openzev.api.model.CreatableInvoiceDto;
import com.mav.openzev.api.model.InvoiceDto;
import com.mav.openzev.api.model.UpdatableInvoiceDto;
import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.mapper.InvoiceMapper;
import com.mav.openzev.model.Accounting;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Unit;
import com.mav.openzev.repository.AccountingRepository;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.repository.UnitRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InvoiceController implements InvoiceApi {

  private final AccountingRepository accountingRepository;
  private final InvoiceRepository invoiceRepository;
  private final UnitRepository unitRepository;

  private final InvoiceMapper invoiceMapper;

  @Override
  @Transactional
  public ResponseEntity<UUID> createInvoice(final CreatableInvoiceDto creatableInvoiceDto) {
    final Accounting accounting =
        accountingRepository
            .findByUuid(creatableInvoiceDto.getAccountingId())
            .orElseThrow(
                () ->
                    NotFoundException.ofAccountingNotFound(creatableInvoiceDto.getAccountingId()));

    final Unit unit =
        unitRepository
            .findByUuid(creatableInvoiceDto.getUnitId())
            .orElseThrow(() -> NotFoundException.ofUnitNotFound(creatableInvoiceDto.getUnitId()));

    final Invoice invoice = invoiceMapper.mapToInvoice(creatableInvoiceDto);
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
      final UUID invoiceId, final UpdatableInvoiceDto updatableInvoiceDto) {
    final Invoice invoice =
        invoiceRepository
            .findByUuid(invoiceId)
            .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));

    invoiceMapper.updateInvoice(updatableInvoiceDto, invoice);
    return ResponseEntity.ok(invoiceRepository.save(invoice).getUuid());
  }
}
