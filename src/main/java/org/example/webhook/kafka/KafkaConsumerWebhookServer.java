package org.example.webhook.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.webhook.WebhookServer;
import org.example.webhook.event.WebhookEvent;
import org.example.webhook.http.WebhookHttpClient;
import org.example.webhook.kafka.serialization.JacksonObjectMapperKafkaValueDeserializer;
import org.example.webhook.retry.WebhookHttpRetryer;
import org.example.webhook.retry.WebhookRetryer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaConsumerWebhookServer extends WebhookServer {
    private final KafkaConsumer<String, WebhookEvent> kafkaConsumer;

    private final ExecutorService executorService;

    private final WebhookRetryer webhookRetryer;


    public KafkaConsumerWebhookServer(String... eventTypes) {
        this(Arrays.asList(eventTypes));
    }

    public KafkaConsumerWebhookServer(List<String> eventTypes) {
        super(eventTypes);
        Properties config = getProperties();
        kafkaConsumer = new KafkaConsumer<>(config);
        executorService = Executors.newVirtualThreadPerTaskExecutor();
        webhookRetryer = new WebhookHttpRetryer(new WebhookHttpClient());
    }

    private Properties getProperties() {
        try {
            Properties config = new Properties();
            config.put(CommonClientConfigs.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
            config.put(CommonClientConfigs.GROUP_ID_CONFIG, "foo");
            config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);
            config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaValueDeserializer.class);
            return config;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        kafkaConsumer.subscribe(getEventTypes());
        executorService.submit(this::pollAndConsume);
    }

    private void pollAndConsume() {
        while (true) {
            System.out.println("Polling... " + Instant.now());
            ConsumerRecords<String, WebhookEvent> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));
            consumerRecords.forEach(this::handleConsumerRecord);
            kafkaConsumer.commitSync(Duration.ofSeconds(1));
        }
    }

    private void handleConsumerRecord(ConsumerRecord<String, WebhookEvent> consumerRecord) {
        try {
            WebhookEvent webhookEvent = consumerRecord.value();
            webhookRetryer.attempt(webhookEvent.command(), webhookEvent.retryConfig());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
