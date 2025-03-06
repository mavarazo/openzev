package com.mav.openzev.exception;

import com.mav.openzev.entity.Reading;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValidationExcpetion extends ResponseStatusException {

  public ValidationExcpetion(
      final String reason, final String messageDetailCode, final Object... messageDetailArgs) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, reason, null, messageDetailCode, messageDetailArgs);
  }

  public static ValidationExcpetion readingDuplicated(Reading reading) {
    return new ValidationExcpetion(
        "Reading per '%s' for meter point '%s' already exists"
            .formatted(reading.getDate(), reading.getMeterPoint().getNumber()),
        "reading_duplicated");
  }

  public static ValidationExcpetion readingTotalIncorrect(final Reading reading) {
    return new ValidationExcpetion(
        "Reading total '%s' incorrect: '%s'"
            .formatted(
                reading.getTotal(),
                Stream.of(reading.getPeakTariff(), reading.getOffPeakTariff())
                    .map(BigDecimal::toString)
                    .collect(Collectors.joining(" + "))),
        "reading_total_incorrect",
        reading.getPeakTariff(),
        reading.getOffPeakTariff(),
        reading.getTotal());
  }
}
