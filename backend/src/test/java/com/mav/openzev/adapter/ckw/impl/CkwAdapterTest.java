package com.mav.openzev.adapter.ckw.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import com.mav.openzev.adapter.ckw.CkwAdapter;
import com.mav.openzev.adapter.ckw.CkwAdapterConfig;
import com.mav.openzev.adapter.ckw.model.ChronoUnit;
import com.mav.openzev.adapter.ckw.model.Consumption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {CkwAdapter.class, CkwAdapterConfig.class})
@EnableWireMock({@ConfigureWireMock(name = "ckw-service", property = "openzev.ckw.base-url")})
class CkwAdapterTest {

  @InjectWireMock("ckw-service")
  private WireMockServer wiremock;

  @Autowired private CkwAdapter sut;

  @Nested
  class GetOwnerConsumptionsTests {

    @Test
    void status200() {
      // arrange
      wiremock.stubFor(
          get(anyUrl())
              .willReturn(
                  okJson(
                      """
[
  {
    "anzahl_linien_fb": 0,
    "anzahl_linien_fw": 1,
    "anzahl_linien_p": 1,
    "betrag_blind_ht": null,
    "betrag_blind_nt": null,
    "betrag_ht": 1.1629799981601536,
    "betrag_nt": 0.3809800073504448,
    "max_leistung_faktura": 1.6959999799728394,
    "max_leistung_physisch": 1.6959999799728394,
    "menge_fakturiert_blind_ht": null,
    "menge_fakturiert_blind_nt": null,
    "menge_fakturiert_ht": 4.614999987185001,
    "menge_fakturiert_nt": 1.7720000334084034,
    "menge_physikalisch": 6.387000020593405,
    "zeitstempel_bis_utc": "Thu, 01 Jun 2023 22:00:00 GMT",
    "zeitstempel_von_utc": "Wed, 31 May 2023 22:00:00 GMT"
  }
]
""")));

      // act
      final List<Consumption> result =
          sut.getConsumption(
              "12345",
              "56789",
              LocalDate.of(2023, 5, 31),
              LocalDate.of(2023, 5, 31),
              ChronoUnit.DAY);

      // assert
      assertThat(result)
          .hasSize(1)
          .singleElement()
          .returns(0, Consumption::anzahlLinienFb)
          .returns(1, Consumption::anzahlLinienFw)
          .returns(1, Consumption::anzahlLinienP)
          .returns(null, Consumption::betragBlindHt)
          .returns(null, Consumption::betragBlindNt)
          .returns(1.1629799981601536, Consumption::betragHt)
          .returns(0.3809800073504448, Consumption::betragNt)
          .returns(1.6959999799728394, Consumption::maxLeistungFaktura)
          .returns(1.6959999799728394, Consumption::maxLeistungPhysisch)
          .returns(null, Consumption::mengeFakturiertBlindHt)
          .returns(null, Consumption::mengeFakturiertBlindNt)
          .returns(4.614999987185001, Consumption::mengeFakturiertHt)
          .returns(1.7720000334084034, Consumption::mengeFakturiertNt)
          .returns(6.387000020593405, Consumption::mengePhysikalisch)
          .returns(LocalDateTime.of(2023, 6, 1, 22, 0, 0, 0), Consumption::zeitstempelBisUtc)
          .returns(LocalDateTime.of(2023, 5, 31, 22, 0, 0, 0), Consumption::zeitstempelVonUtc);
    }
  }
}
