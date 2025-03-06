package com.mav.openzev.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateReadingCommand(
    UUID meterPointId,
    LocalDate date,
    BigDecimal peakTariff,
    BigDecimal offPeakTariff,
    BigDecimal total) {}
