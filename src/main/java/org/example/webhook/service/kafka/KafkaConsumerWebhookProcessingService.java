package org.example.webhook.service.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.webhook.domain.event.EventType;
import org.example.webhook.domain.event.HttpCommand;
import org.example.webhook.domain.event.WebhookEvent;
import org.example.webhook.service.WebhookProcessingService;
import org.example.webhook.service.http.WebhookHttpClient;
import org.example.webhook.service.kafka.serialization.JacksonObjectMapperKafkaValueDeserializer;
import org.example.webhook.service.retry.WebhookHttpRetryer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

public class KafkaConsumerWebhookProcessingService extends WebhookProcessingService {
    private final KafkaConsumer<String, WebhookEvent> kafkaConsumer;

    private final WebhookHttpRetryer webhookHttpRetryer;

    public KafkaConsumerWebhookProcessingService(EventType eventType) {
        super(eventType);
        Properties config = getProperties();
        kafkaConsumer = new KafkaConsumer<>(config);
        webhookHttpRetryer = new WebhookHttpRetryer(new WebhookHttpClient());
    }

    private Properties getProperties() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);

        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "webhook_consumer" + UUID.randomUUID());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer_group");
        config.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "main_webhook_consumer_instance");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaValueDeserializer.class);

        return config;
    }

    @Override
    public void start() {
        kafkaConsumer.subscribe(Collections.singleton(eventType.id()));
    }

    @Override
    public Optional<WebhookEvent> pollAndConsume() {
        System.out.println("Polling... " + Instant.now());
        ConsumerRecords<String, WebhookEvent> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(500));

        List<WebhookEvent> webhookEvents = new ArrayList<>();
        for (ConsumerRecord<String, WebhookEvent> consumerRecord : consumerRecords) {
            webhookEvents.add(handleConsumerRecord(consumerRecord));
        }
        kafkaConsumer.commitSync();

        return webhookEvents.stream().findAny();
    }

    private WebhookEvent handleConsumerRecord(ConsumerRecord<String, WebhookEvent> consumerRecord) {
        try {
            System.out.println("Handling offset: " + consumerRecord.offset());
            WebhookEvent webhookEvent = consumerRecord.value();

            switch (webhookEvent.command()) {
                case HttpCommand httpCommand -> {
                    webhookHttpRetryer.attemptWithRetry(httpCommand, eventType.retryConfig());
                }
            }

            return webhookEvent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
