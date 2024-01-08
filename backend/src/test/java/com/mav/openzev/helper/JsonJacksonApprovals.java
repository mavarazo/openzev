package com.mav.openzev.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spun.util.ObjectUtils;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.scrubbers.GuidScrubber;
import org.approvaltests.scrubbers.RegExScrubber;
import org.approvaltests.scrubbers.Scrubbers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonJacksonApprovals {

  @Autowired private ObjectMapper objectMapper;

  public void verifyAsJson(final Object o) {
    verifyAsJson(o, new Options());
  }

  public void verifyAsJson(final Object o, final Options options) {
    Approvals.verify(
        asJson(o),
        options
            .withScrubber(Scrubbers.scrubAll(new GuidScrubber(), new LocalDateTimeScrubber()))
            .forFile()
            .withExtension(".json"));
  }

  public String asJson(final Object o) {
    try {
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    } catch (final JsonProcessingException var2) {
      throw ObjectUtils.throwAsError(var2);
    }
  }

  private static class LocalDateTimeScrubber extends RegExScrubber {

    public LocalDateTimeScrubber() {
      super(
          "\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}(:\\d{2})?(\\.\\d+)?(([+-]\\d{2}:\\d{2})|Z)?)?",
          (n) -> "localdate(time)_" + n);
    }
  }
}
