package com.mav.openzev.adapter.qrgenerator.model.validator;

public record ValidationMessage(
    String type, String field, String messageKey, String messageParameters) {}
