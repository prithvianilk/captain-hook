package com.prithvianilk.captainhook.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import com.prithvianilk.captainhook.domain.RetryConfig;

@Converter
public class RetryConfigToStringConverter implements AttributeConverter<RetryConfig, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(RetryConfig attribute) {
        return OBJECT_MAPPER.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public RetryConfig convertToEntityAttribute(String dbData) {
        return OBJECT_MAPPER.readValue(dbData, RetryConfig.class);
    }
}
