package com.mav.openzev.adapter.ckw.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChronoUnit {
    YEAR("year"),
    MONTH("month"),
    DAY("day"),
    HOUR("hour"),
    QUARTER_HOUR("quarter_hour");

  private final String value;

  ChronoUnit(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
