package com.prithvianilk.captainhook.service.kafka.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import com.prithvianilk.captainhook.domain.WebhookEvent;

import java.io.IOException;

@Slf4j
public class JacksonObjectMapperKafkaWebhookEventValueDeserializer implements Deserializer<WebhookEvent> {
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
