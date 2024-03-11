package com.mav.openzev.controller;


import com.mav.openzev.api.InvoiceApi;
import com.mav.openzev.api.model.InvoiceDto;
import com.mav.openzev.api.model.ModifiableInvoiceDto;
import com.mav.openzev.mapper.InvoiceMapper;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.service.InvoiceService;
import com.mav.openzev.service.OwnerService;
import com.mav.openzev.service.UnitService;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InvoiceController implements InvoiceApi {

  private final InvoiceService invoiceService;
  private final OwnerService ownerService;
  private final UnitService unitService;

  private final InvoiceRepository invoiceRepository;

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
    unitService.findUnit(modifiableInvoiceDto.getUnitId()).ifPresent(invoice::setUnit);
    invoice.setRecipient(ownerService.findOwnerOrFail(modifiableInvoiceDto.getRecipientId()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  public ResponseEntity<InvoiceDto> getInvoice(final UUID invoiceId) {
    final Invoice invoice = invoiceService.findInvoiceOrFail(invoiceId);
    return ResponseEntity.ok(invoiceMapper.mapToInvoiceDto(invoice));
  }

  @Override
  @Transactional
  public ResponseEntity<UUID> changeInvoice(
      final UUID invoiceId, final ModifiableInvoiceDto modifiableInvoiceDto) {
    final Invoice invoice = invoiceService.findInvoiceOrFail(invoiceId);
    invoiceMapper.updateInvoice(modifiableInvoiceDto, invoice);
    unitService.findUnit(modifiableInvoiceDto.getUnitId()).ifPresent(invoice::setUnit);
    invoice.setRecipient(ownerService.findOwnerOrFail(modifiableInvoiceDto.getRecipientId()));
    return ResponseEntity.ok(invoiceRepository.save(invoice).getUuid());
  }

  @Override
  public ResponseEntity<Void> deleteInvoice(final UUID invoiceId) {
    invoiceRepository.delete(invoiceService.findInvoiceOrFail(invoiceId));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Resource> getPdf(final UUID invoiceId) {
    final Invoice invoice = invoiceService.findInvoiceOrFail(invoiceId);
    final ByteArrayInputStream byteArrayInputStream = invoiceService.generatePdf(invoiceId);

    final var headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION,
        "inline; filename=OpenZEV - %s - %s %s.pdf"
            .formatted(
                LocalDate.now().toString(),
                invoice.getRecipient().getFirstName(),
                invoice.getRecipient().getLastName()));

    return ResponseEntity.ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_PDF)
        .body(new InputStreamResource(byteArrayInputStream));
  }

  @Override
  public ResponseEntity<Void> sendEmail(final UUID invoiceId) {
    invoiceService.sendAsEmail(invoiceId);
    return ResponseEntity.noContent().build();
  }
}
