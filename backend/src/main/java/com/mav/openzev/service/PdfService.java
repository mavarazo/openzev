package com.mav.openzev.service;

import com.lowagie.text.DocumentException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xhtmlrenderer.pdf.ITextRenderer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class PdfService {

  public static ByteArrayInputStream generatePdf(final String content) {
    ByteArrayInputStream byteArrayInputStream = null;

    try {
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      final ITextRenderer renderer = new ITextRenderer();
      renderer.setDocumentFromString(content);
      renderer.layout();
      renderer.createPDF(byteArrayOutputStream);
      byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    } catch (final DocumentException e) {
      log.error(e.getMessage(), e);
    }
    return byteArrayInputStream;
  }
}
