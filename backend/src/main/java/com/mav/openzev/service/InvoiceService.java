package com.mav.openzev.service;

import com.mav.openzev.exception.NotFoundException;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.repository.BankAccountRepository;
import com.mav.openzev.repository.InvoiceRepository;
import com.mav.openzev.repository.RepresentativeRepository;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class InvoiceService {

  private static final String INVOICE_PDF = "pdf/invoice.html";

  private final InvoiceRepository invoiceRepository;
  private final RepresentativeRepository representativeRepository;
  private final BankAccountRepository bankAccountRepository;

  private final TemplateEngine templateEngine;

  public ByteArrayInputStream generatePdf(final Invoice invoice) {
    final Context context = new Context();
    context.setVariables(
        Map.of(
            "created",
            invoice.getCreated(),
            "dueDate",
            invoice.getDueDate(),
            "recipient",
            invoice.getRecipient(),
            "items",
            invoice.getItems(),
            "total",
            invoice.getTotal(),
            "representative",
            representativeRepository
                .findActive()
                .orElseThrow(NotFoundException::ofRepresentativeActive),
            "bankAccount",
            bankAccountRepository
                .findActive()
                .orElseThrow(NotFoundException::ofBankAccountActive)));
    return PdfService.generatePdf(templateEngine.process(INVOICE_PDF, context));
  }

  public Invoice findInvoiceOrFail(final UUID invoiceId) {
    return invoiceRepository
        .findByUuid(invoiceId)
        .orElseThrow(() -> NotFoundException.ofInvoiceNotFound(invoiceId));
  }
}
