package com.prithvianilk.captainhook.service.kafka.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.domain.WebhookEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@Slf4j
public class JacksonObjectMapperKafkaEventTypeValueDeserializer implements Deserializer<EventType> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public EventType deserialize(String topic, byte[] data) {
        try {
            return OBJECT_MAPPER.readValue(data, EventType.class);
        } catch (IOException e) {
            log.error("Deserialization failed", e);
            throw new RuntimeException(e);
        }
    }
}
