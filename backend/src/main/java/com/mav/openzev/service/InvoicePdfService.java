package com.mav.openzev.service;

import com.mav.openzev.adapter.qrgenerator.QrGeneratorAdapter;
import com.mav.openzev.adapter.qrgenerator.model.generator.Creditor;
import com.mav.openzev.adapter.qrgenerator.model.generator.Debitor;
import com.mav.openzev.adapter.qrgenerator.model.generator.Format;
import com.mav.openzev.adapter.qrgenerator.model.generator.Payment;
import com.mav.openzev.adapter.qrgenerator.model.generator.Request;
import com.mav.openzev.model.*;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoicePdfService {

  private static final String INVOICE_PDF = "pdf/invoice.html";

  private final TemplateEngine templateEngine;
  private final QrGeneratorAdapter qrGeneratorAdapter;

  public byte[] generatePdf(final Invoice invoice, final Representative representative) {
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
            representative));

    return PdfService.generatePdf(templateEngine.process(INVOICE_PDF, context));
  }

  public byte[] generateQrBill(
      final Invoice invoice, final Settings settings, final BankAccount bankAccount) {
    final Request request =
        new Request(
            buildCreditorRequest(bankAccount, settings),
            buildDebitorRequest(invoice.getRecipient()),
            buildFormatRequest(),
            buildPaymentRequest(invoice));
    return qrGeneratorAdapter.get(request);
  }

  private Creditor buildCreditorRequest(final BankAccount bankAccount, final Settings settings) {
    return Creditor.builder()
        .account(bankAccount.getIban())
        .name(settings.getName())
        .street(settings.getStreet())
        .houseNo(settings.getHouseNr())
        .postalCode(settings.getPostalCode())
        .town(settings.getCity())
        .build();
  }

  private Debitor buildDebitorRequest(final Owner recipient) {
    return Debitor.builder()
        .name("%s %s".formatted(recipient.getFirstName(), recipient.getLastName()))
        .street(recipient.getStreet())
        .houseNo(recipient.getHouseNr())
        .postalCode(recipient.getPostalCode())
        .town(recipient.getCity())
        .build();
  }

  private Format buildFormatRequest() {
    return Format.builder()
        .outputSize(Format.OutputSize.A4_PORTRAIT_SHEET)
        .graphicsFormat(Format.GraphicsFormat.PDF)
        .build();
  }

  private Payment buildPaymentRequest(final Invoice invoice) {
    return Payment.builder()
        .amount(invoice.getTotal().doubleValue())
        .unstructuredMessage(invoice.getUuid().toString())
        .build();
  }
}
