package com.mav.openzev.adapter.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.mav.openzev.adapter.CkwAdapterConfig;
import com.mav.openzev.adapter.model.ChronoUnit;
import com.mav.openzev.adapter.model.Consumption;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Mono;

@SpringJUnitConfig(classes = {CkwAdapterImpl.class, CkwAdapterConfig.class})
@TestPropertySource(properties = {"openzev.ckw.base-url=http://localhost:2222/"})
class CkwAdapterImplTest {

  @Autowired private CkwAdapterImpl sut;

  public static MockWebServer mockBackEnd;

  @BeforeAll
  static void setUp() throws IOException {
    mockBackEnd = new MockWebServer();
    mockBackEnd.start(2222);
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockBackEnd.shutdown();
  }

  @Nested
  class GetOwnerConsumptionsTests {

    @Test
    void status200() {
      // arrange
      mockBackEnd.enqueue(
          new MockResponse()
              .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .setResponseCode(200)
              .setBody(
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
"""));

      // act
      final Mono<List<Consumption>> result =
          sut.getConsumption(
              "12345",
              "56789",
              LocalDate.of(2023, 5, 31),
              LocalDate.of(2023, 5, 31),
              ChronoUnit.DAY);

      // assert
      assertThat(result.block())
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
          .returns(
              LocalDateTime.of(2023, 6, 1, 22, 0, 0, 0),
              Consumption::zeitstempelBisUtc)
          .returns(
                  LocalDateTime.of(2023, 5, 31, 22, 0, 0, 0),
              Consumption::zeitstempelVonUtc);
    }
  }
}
