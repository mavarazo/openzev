package com.mav.openzev.consumption.model;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ChangeConsumptionCommand(
    UUID id,
    UUID meterPointId,
    @Nullable UUID previousConsumptionId,
    LocalDate date,
    BigDecimal total) {}
