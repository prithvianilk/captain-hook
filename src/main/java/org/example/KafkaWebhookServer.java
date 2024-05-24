package org.example;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaWebhookServer extends WebhookServer {
    private final KafkaConsumer<String, WebhookEvent> kafkaConsumer;

    private final ExecutorService executorService;

    public KafkaWebhookServer(String... eventTypes) {
        this(Arrays.asList(eventTypes));
    }

    public KafkaWebhookServer(List<String> eventTypes) {
        super(eventTypes);
        Properties config = getProperties();
        kafkaConsumer = new KafkaConsumer<>(config);
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    private Properties getProperties() {
        try {
            Properties config = new Properties();
            config.put(CommonClientConfigs.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
            config.put(CommonClientConfigs.GROUP_ID_CONFIG, "foo");
            config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaValueDeserializer.class);
            return config;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void start() {
        kafkaConsumer.subscribe(getEventTypes());
        executorService.submit(this::pollAndConsume);
    }

    private void pollAndConsume() {
        while (true) {
            System.out.println("Polling...");
            ConsumerRecords<String, WebhookEvent> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));
            consumerRecords.forEach(System.out::println);
            kafkaConsumer.commitSync(Duration.ofSeconds(1));
        }
    }
}
