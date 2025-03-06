package com.mav.openzev.unit.model;

import java.util.UUID;

public record ChangeUnitCommand(UUID id, String number, String firstName, String lastName) {}
