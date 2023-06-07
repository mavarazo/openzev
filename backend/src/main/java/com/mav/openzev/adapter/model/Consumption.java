package com.mav.openzev.adapter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record Consumption(
    @JsonProperty("anzahl_linien_fb") Integer anzahlLinienFb,
    @JsonProperty("anzahl_linien_fw") Integer anzahlLinienFw,
    @JsonProperty("anzahl_linien_p") Integer anzahlLinienP,
    @JsonProperty("betrag_blind_ht") Double betragBlindHt,
    @JsonProperty("betrag_blind_nt") Double betragBlindNt,
    @JsonProperty("betrag_ht") Double betragHt,
    @JsonProperty("betrag_nt") Double betragNt,
    @JsonProperty("max_leistung_faktura") Double maxLeistungFaktura,
    @JsonProperty("max_leistung_physisch") Double maxLeistungPhysisch,
    @JsonProperty("menge_fakturiert_blind_ht") Double mengeFakturiertBlindHt,
    @JsonProperty("menge_fakturiert_blind_nt") Double mengeFakturiertBlindNt,
    @JsonProperty("menge_fakturiert_ht") Double mengeFakturiertHt,
    @JsonProperty("menge_fakturiert_nt") Double mengeFakturiertNt,
    @JsonProperty("menge_physikalisch") Double mengePhysikalisch,
    @JsonProperty("zeitstempel_bis_utc") @JsonDeserialize(using = ZeitstempelUtcDeserializer.class)
    LocalDateTime zeitstempelBisUtc,
    @JsonProperty("zeitstempel_von_utc") @JsonDeserialize(using = ZeitstempelUtcDeserializer.class)
    LocalDateTime zeitstempelVonUtc) {}
