package com.prithvianilk.captainhook.service.kafka.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prithvianilk.captainhook.domain.WebhookEvent;
import org.apache.kafka.common.serialization.Serializer;

public class JacksonObjectMapperKafkaWebhookEventValueSerializer implements Serializer<WebhookEvent> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, WebhookEvent data) {
        try {
            String dataAsString = OBJECT_MAPPER.writeValueAsString(data);
            return dataAsString.getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
