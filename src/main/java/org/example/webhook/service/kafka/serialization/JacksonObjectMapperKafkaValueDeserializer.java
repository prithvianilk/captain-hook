package org.example.webhook.service.kafka.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.webhook.domain.WebhookEvent;

import java.io.IOException;

@Slf4j
public class JacksonObjectMapperKafkaValueDeserializer implements Deserializer<WebhookEvent> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public WebhookEvent deserialize(String topic, byte[] data) {
        try {
            return OBJECT_MAPPER.readValue(data, WebhookEvent.class);
        } catch (IOException e) {
            log.error("Deserialization failed", e);
            throw new RuntimeException(e);
        }
    }
}
