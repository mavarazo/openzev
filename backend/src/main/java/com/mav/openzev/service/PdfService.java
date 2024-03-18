package com.mav.openzev.service;

import static java.util.Objects.nonNull;

import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.xhtmlrenderer.pdf.ITextRenderer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class PdfService {

  public static byte[] generatePdf(final String content) {
    try {
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      final ITextRenderer renderer = new ITextRenderer();

      renderer.setDocumentFromString(content);
      renderer.layout();
      renderer.createPDF(byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    } catch (final DocumentException e) {
      log.error(e.getMessage(), e);
    }
    return new byte[0];
  }

  @SneakyThrows
  public static byte[] mergePdf(final List<byte[]> pdfsAsBytes) {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
    try (final PDDocument mergedPdf = new PDDocument()) {
      for (final byte[] pdfAsBytes : pdfsAsBytes) {
        if (nonNull(pdfAsBytes) && pdfAsBytes.length > 0) {
          pdfMergerUtility.appendDocument(mergedPdf, Loader.loadPDF(pdfAsBytes));
        }
      }
      mergedPdf.save(byteArrayOutputStream);
    }
    return byteArrayOutputStream.toByteArray();
  }
}
