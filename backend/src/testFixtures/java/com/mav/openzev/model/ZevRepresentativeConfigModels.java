package com.mav.openzev.model;

import com.mav.openzev.model.config.ZevRepresentativeConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZevRepresentativeConfigModels {

  public static final java.util.UUID UUID =
      java.util.UUID.fromString("1bc1addb-aab6-4374-826b-7bc8f4f71127");
  public static final String STRADUN = "Stradun";
  public static final String _30 = "30";
  public static final String _1624 = "1624";
  public static final String GRATTAVACHE = "Grattavache";
  public static final String ANNA = "Anna";
  public static final String BARRY = "Barry";
  public static final String ANNA_BARRY_COM = "anna@barry.com";
  public static final String _41415556677 = "+41 41 555 66 77";
  public static final String _41795556677 = "+41 79 555 66 77";

  public static ZevRepresentativeConfig getZevRepresentativeConfig() {
    return ZevRepresentativeConfig.builder()
        .uuid(UUID)
        .firstName(ANNA)
        .lastName(BARRY)
        .email(ANNA_BARRY_COM)
        .street(STRADUN)
        .houseNr(_30)
        .postalCode(_1624)
        .city(GRATTAVACHE)
        .phoneNr(_41415556677)
        .mobileNr(_41795556677)
        .build();
  }
}
