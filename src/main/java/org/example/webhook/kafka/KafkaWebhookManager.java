package org.example.webhook.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.example.webhook.WebhookManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KafkaWebhookManager implements WebhookManager {
    private final AdminClient adminClient;

    public KafkaWebhookManager() {
        Properties config = getProperties();
        adminClient = AdminClient.create(config);
    }

    @Override
    public void registerNewEventTypes(List<String> eventTypes) {
        List<NewTopic> newTopics = eventTypes
                .stream()
                .map(eventType -> new NewTopic(eventType, 1, (short) 1))
                .toList();

        adminClient.createTopics(newTopics);
    }

    @Override
    public Collection<String> getRegisteredEventTypes() {
        try {
            return adminClient.listTopics().names().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Properties getProperties() {
        Properties config = new Properties();
        config.put(CommonClientConfigs.CLIENT_ID_CONFIG, "webhook_manager:" + UUID.randomUUID());
        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);
        return config;
    }
}
