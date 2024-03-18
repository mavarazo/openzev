package com.mav.openzev.adapter.ckw.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ZeitstempelUtcDeserializer extends JsonDeserializer<LocalDateTime> {

  // Thu, 01 Jun 2023 22:00:00 GMT
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

  @Override
  public LocalDateTime deserialize(
      final JsonParser jsonParser, final DeserializationContext deserializationContext)
      throws IOException {
    final String dateAsString = jsonParser.getText();
    if (dateAsString == null) {
      throw new IOException("OffsetDateTime argument is null.");
    }
    return LocalDateTime.parse(dateAsString, DATE_TIME_FORMATTER);
  }
}
