package com.mav.openzev.service;

import com.mav.openzev.exception.BadRequestException;
import com.mav.openzev.model.AbstractEntity;
import com.mav.openzev.model.Document;
import com.mav.openzev.repository.DocumentRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentService {

  private static final List<MediaType> SUPPORTED_MEDIA_TYPES = List.of(MediaType.APPLICATION_PDF);

  private final DocumentRepository documentRepository;

  @SneakyThrows
  public <E extends AbstractEntity> Document createDocument(
      final MultipartFile content, final E entity) {
    final Document document = new Document();
    document.setRefId(entity.getId());
    document.setRefType(entity.getClass().getSimpleName());
    document.setName(content.getName());
    document.setFilename(content.getOriginalFilename());
    document.setMediaType(content.getContentType());
    document.setData(content.getBytes());
    document.setThumbnail(generateThumbnail(getMediaType(content), content.getBytes()));

    return documentRepository.save(document);
  }

  private static MediaType getMediaType(final MultipartFile content) {
    try {
      final MediaType mediaType =
          MediaType.valueOf(Objects.requireNonNull(content.getContentType()));
      if (SUPPORTED_MEDIA_TYPES.contains(mediaType)) {
        return mediaType;
      } else {
        throw BadRequestException.ofUnsupportedMediaType(content.getContentType());
      }
    } catch (final InvalidMediaTypeException ex) {
      throw BadRequestException.ofUnknownMediaType(content.getContentType());
    }
  }

  @SneakyThrows
  private static byte[] generateThumbnail(final MediaType mediaType, final byte[] bytes) {
    if (MediaType.APPLICATION_PDF.equals(mediaType)) {
      try (final PDDocument document = Loader.loadPDF(bytes)) {

        final PDFRenderer pdfRenderer = new PDFRenderer(document);
        final BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 72);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        final byte[] thumbnail = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return thumbnail;
      }
    }
    return new byte[0];
  }
}
