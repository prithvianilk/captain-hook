package com.prithvianilk.captainhook.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import com.prithvianilk.captainhook.domain.Command;

@Converter
public class CommandToStringConverter implements AttributeConverter<Command, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Command attribute) {
        return OBJECT_MAPPER.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public Command convertToEntityAttribute(String dbData) {
        return OBJECT_MAPPER.readValue(dbData, Command.class);
    }
}
