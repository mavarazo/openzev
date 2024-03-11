package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.exception.ValidationException;
import com.mav.openzev.model.BankAccount;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.InvoiceStatus;
import com.mav.openzev.model.Representative;
import com.mav.openzev.repository.InvoiceRepository;
import java.io.ByteArrayInputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceService {
  
  private final InvoiceRepository invoiceRepository;

  private final RepresentativeService representativeService;
  private final BankAccountService bankAccountService;

  private final InvoicePdfService invoicePdfService;
  private final InvoiceEmailService invoiceEmailService;

  public Invoice findInvoiceOrFail(final UUID invoiceId) {
    return invoiceRepository
        .findByUuid(invoiceId)
        .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));
  }

  @Transactional(readOnly = true)
  public ByteArrayInputStream generatePdf(final UUID invoiceId) {
    final Invoice invoice = findInvoiceOrFail(invoiceId);

    final Representative representative = representativeService.findActive();
    final BankAccount bankAccount = bankAccountService.findActive();

    return invoicePdfService.generatePdf(invoice, representative, bankAccount);
  }

  @Transactional(readOnly = true)
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
  }
}
