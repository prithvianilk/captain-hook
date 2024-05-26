package com.prithvianilk.captainhook.service.kafka.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prithvianilk.captainhook.domain.EventType;
import org.apache.kafka.common.serialization.Serializer;

public class JacksonObjectMapperKafkaEventTypeValueSerializer implements Serializer<EventType> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, EventType data) {
        try {
            String dataAsString = OBJECT_MAPPER.writeValueAsString(data);
            return dataAsString.getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
