package org.example.webhook.service.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.webhook.service.WebhookCreationClient;
import org.example.webhook.domain.event.WebhookEvent;
import org.example.webhook.service.kafka.serialization.JacksonObjectMapperKafkaValueSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class KafkaProducerWebhookCreationClient implements WebhookCreationClient {
    private final KafkaProducer<String, WebhookEvent> kafkaProducer;

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
            ProducerRecord<String, WebhookEvent> producerRecord = new ProducerRecord<>(eventType, webhookEvent.eventId(), webhookEvent);
            RecordMetadata recordMetadata = kafkaProducer.send(producerRecord).get(5, TimeUnit.SECONDS);
            System.out.println("Published: " + recordMetadata);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
