package com.mav.openzev.meter_point.model;

import java.util.UUID;

public record ChangeMeterPointCommand(UUID id, UUID unitId, String number) {}
