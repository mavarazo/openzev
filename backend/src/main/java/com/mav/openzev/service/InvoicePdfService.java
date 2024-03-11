package com.mav.openzev.service;

import com.mav.openzev.model.BankAccount;
import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Representative;
import java.io.ByteArrayInputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

  private static final String INVOICE_PDF = "pdf/invoice.html";

  private final TemplateEngine templateEngine;

  public ByteArrayInputStream generatePdf(
      final Invoice invoice, final Representative representative, final BankAccount bankAccount) {
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
            representative,
            "bankAccount",
            bankAccount));
    return PdfService.generatePdf(templateEngine.process(INVOICE_PDF, context));
  }
}
