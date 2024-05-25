package org.example.webhook.service.kafka.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.example.webhook.domain.WebhookEvent;

public class JacksonObjectMapperKafkaValueSerializer implements Serializer<WebhookEvent> {
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
