package com.mav.openzev.adapter.qrgenerator.model.validator;

import java.util.List;

public record Response(List<ValidationMessage> validationMessages) {}
