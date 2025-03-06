package com.mav.openzev.meter_point_reading.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMeterPointReadingCommand(
    UUID readingId,
    UUID meterPointId,
    BigDecimal peakTariff,
    BigDecimal offPeakTariff,
    BigDecimal total) {}
