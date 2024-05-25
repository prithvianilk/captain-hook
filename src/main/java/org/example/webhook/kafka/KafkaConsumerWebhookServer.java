package org.example.webhook.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.webhook.WebhookServer;
import org.example.webhook.event.HttpCommand;
import org.example.webhook.event.WebhookEvent;
import org.example.webhook.http.WebhookHttpClient;
import org.example.webhook.kafka.serialization.JacksonObjectMapperKafkaValueDeserializer;
import org.example.webhook.retry.WebhookHttpRetryer;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaConsumerWebhookServer extends WebhookServer {
    private final KafkaConsumer<String, WebhookEvent> kafkaConsumer;

    private final ExecutorService executorService;

    private final WebhookHttpRetryer webhookHttpRetryer;

    public KafkaConsumerWebhookServer(String... eventTypes) {
        this(Arrays.asList(eventTypes));
    }

    public KafkaConsumerWebhookServer(List<String> eventTypes) {
        super(eventTypes);
        Properties config = getProperties();
        kafkaConsumer = new KafkaConsumer<>(config);
        executorService = Executors.newVirtualThreadPerTaskExecutor();
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
        kafkaConsumer.subscribe(getEventTypes());
        executorService.submit(this::pollAndConsume);
    }

    private void pollAndConsume() {
        while (true) {
            System.out.println("Polling... " + Instant.now());
            ConsumerRecords<String, WebhookEvent> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(500));
            consumerRecords.forEach(this::handleConsumerRecord);
            kafkaConsumer.commitSync();
        }
    }

    private void handleConsumerRecord(ConsumerRecord<String, WebhookEvent> consumerRecord) {
        try {
            System.out.println("Handling offset: " + consumerRecord.offset());
            WebhookEvent webhookEvent = consumerRecord.value();

            switch (webhookEvent.command()) {
                case HttpCommand httpCommand -> {
                    webhookHttpRetryer.attemptWithRetry(httpCommand, webhookEvent.retryConfig());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
