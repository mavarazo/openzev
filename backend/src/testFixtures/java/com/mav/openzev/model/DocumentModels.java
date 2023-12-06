package com.mav.openzev.model;

import java.nio.charset.StandardCharsets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("2ce14304-c6bf-440a-9673-f5bfdf96982c");

  public static Document getDocument() {
    return Document.builder()
        .uuid(UUID)
        .name("foo")
        .filename("foo.pdf")
        .mediaType("application/pdf")
        .data("lorem ipsum".getBytes(StandardCharsets.UTF_8))
        .thumbnail("dolor".getBytes(StandardCharsets.UTF_8))
        .build();
  }
}
