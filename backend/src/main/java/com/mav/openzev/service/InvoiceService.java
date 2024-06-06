package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.model.*;
import com.mav.openzev.repository.InvoiceRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

  private final InvoiceRepository invoiceRepository;

  private final RepresentativeService representativeService;
  private final BankAccountService bankAccountService;
  private final SettingService settingService;

  private final InvoicePdfService invoicePdfService;
  private final InvoiceEmailService invoiceEmailService;

  public Invoice findInvoiceOrFail(final UUID invoiceId) {
    return invoiceRepository
        .findByUuid(invoiceId)
        .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));
  }

  @SneakyThrows
  @Transactional(readOnly = true)
  public byte[] generatePdf(final UUID invoiceId) {
    final Invoice invoice = findInvoiceOrFail(invoiceId);
    final Representative representative = representativeService.findActive();
    final byte[] invoiceAsPdf = invoicePdfService.generatePdf(invoice, representative);

    final BankAccount bankAccount = bankAccountService.findActive();
    final Settings settings = settingService.findSettingOrFail();
    final byte[] qrBillAsPdf = invoicePdfService.generateQrBill(invoice, settings, bankAccount);

    final List<byte[]> pdfsAsBytes = new LinkedList<>();
    pdfsAsBytes.add(invoiceAsPdf);
    pdfsAsBytes.add(qrBillAsPdf);
    return PdfService.mergePdf(pdfsAsBytes);
  }

  @Transactional
  public void sendAsEmail(final UUID invoiceId) {
    final Invoice invoice = findInvoiceOrFail(invoiceId);
    if (invoice.getStatus() != InvoiceStatus.DRAFT) {
      throw ValidationException.ofInvoiceHasWrongStatus(invoice, InvoiceStatus.DRAFT);
    }
    if (StringUtils.isEmpty(invoice.getRecipient().getEmail())) {
      throw ValidationException.ofRecipientOfInvoiceHasNoEmail(invoice);
    }

    final Representative representative = representativeService.findActive();

    invoiceEmailService.generateAndSend(invoice, representative, generatePdf(invoiceId));
    invoice.setStatus(InvoiceStatus.SENT);
  }
}
