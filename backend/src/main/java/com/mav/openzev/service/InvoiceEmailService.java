package com.mav.openzev.service;

import com.mav.openzev.model.Invoice;
import com.mav.openzev.model.Representative;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class InvoiceEmailService {

  private static final String INVOICE_EMAIL = "email/invoice.html";

  private final TemplateEngine templateEngine;
  private final JavaMailSender javaMailSender;

  @SneakyThrows
  public void generateAndSend(
      final Invoice invoice,
      final Representative representative,
      final ByteArrayInputStream invoiceAsPdf) {
    final Context context = new Context();
    context.setVariables(
        Map.of(
            "invoice",
            invoice,
            "recipient",
            invoice.getRecipient(),
            "representative",
            representative));

    final String body = templateEngine.process(INVOICE_EMAIL, context);

    final MimeMessage message = javaMailSender.createMimeMessage();
    final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setTo(invoice.getRecipient().getEmail());
    helper.setSubject(invoice.getSubject());
    helper.setText(body, true);
    helper.addAttachment(
        "%s - Rechnung.pdf".formatted(DateTimeFormatter.ISO_DATE.format(invoice.getCreated())),
        new ByteArrayResource(invoiceAsPdf.readAllBytes()));
    javaMailSender.send(message);
  }
}
