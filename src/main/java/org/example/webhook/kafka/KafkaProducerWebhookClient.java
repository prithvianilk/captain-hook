package org.example.webhook.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.webhook.WebhookClient;
import org.example.webhook.event.WebhookEvent;
import org.example.webhook.kafka.serialization.JacksonObjectMapperKafkaValueSerializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;


public class KafkaProducerWebhookClient implements WebhookClient {
    private final KafkaProducer<String, WebhookEvent> kafkaProducer;

    public KafkaProducerWebhookClient() {
        Properties config = getProperties();

        kafkaProducer = new KafkaProducer<>(config);
    }

    private Properties getProperties() {
        try {
            Properties config = new Properties();
            config.put(CommonClientConfigs.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
            config.put(CommonClientConfigs.GROUP_ID_CONFIG, "foo");
            config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaValueSerializer.class);
            return config;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void publish(String eventType, WebhookEvent webhookEvent) {
        ProducerRecord<String, WebhookEvent> producerRecord = new ProducerRecord<>(eventType, webhookEvent);
        kafkaProducer.send(producerRecord);
    }
}
