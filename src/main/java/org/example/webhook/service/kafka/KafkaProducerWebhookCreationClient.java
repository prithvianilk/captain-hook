package org.example.webhook.service.kafka;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.webhook.domain.WebhookEvent;
import org.example.webhook.service.WebhookCreationClient;
import org.example.webhook.service.kafka.serialization.JacksonObjectMapperKafkaValueSerializer;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerWebhookCreationClient implements WebhookCreationClient {
    KafkaProducer<String, WebhookEvent> kafkaProducer;

    public KafkaProducerWebhookCreationClient() {
        Properties config = getProperties();
        kafkaProducer = new KafkaProducer<>(config);
    }

    private Properties getProperties() {
        Properties config = new Properties();
        config.put(CommonClientConfigs.CLIENT_ID_CONFIG, "webhook_producer:" + UUID.randomUUID());
        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaValueSerializer.class);
        return config;
    }

    @Override
    public void publish(String eventType, WebhookEvent webhookEvent) {
        try {
            ProducerRecord<String, WebhookEvent> producerRecord = new ProducerRecord<>(eventType, webhookEvent.id(), webhookEvent);
            RecordMetadata recordMetadata = kafkaProducer.send(producerRecord).get(5, TimeUnit.SECONDS);
            log.info("Published: {}", recordMetadata);
        } catch (Exception e) {
            log.error("Failed to publish", e);
            throw new RuntimeException(e);
        }
    }
}
