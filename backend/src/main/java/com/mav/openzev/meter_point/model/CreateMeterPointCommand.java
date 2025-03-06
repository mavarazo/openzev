package com.mav.openzev.meter_point.model;

import java.util.UUID;

public record CreateMeterPointCommand(UUID unitId, String number) {}
