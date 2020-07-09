package com.zebrunner.reporting.domain.dto.errors;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ErrorCodeSerializer extends JsonSerializer<ErrorCode> {
    @Override
    public void serialize(ErrorCode value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeNumber(value.getCode());
    }
}
