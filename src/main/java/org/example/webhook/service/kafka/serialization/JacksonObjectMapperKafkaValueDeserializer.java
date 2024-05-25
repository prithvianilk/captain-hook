package org.example.webhook.service.kafka.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.webhook.domain.event.WebhookEvent;

import java.io.IOException;

public class JacksonObjectMapperKafkaValueDeserializer implements Deserializer<WebhookEvent> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public WebhookEvent deserialize(String topic, byte[] data) {
        try {
            return OBJECT_MAPPER.readValue(data, WebhookEvent.class);
        } catch (IOException e) {
            System.out.println("Deserialization failed: " + e);
            throw new RuntimeException(e);
        }
    }
}
