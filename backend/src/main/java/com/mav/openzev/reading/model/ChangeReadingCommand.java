package com.mav.openzev.reading.model;

import java.time.LocalDate;
import java.util.UUID;

public record ChangeReadingCommand(UUID id, LocalDate date) {}
