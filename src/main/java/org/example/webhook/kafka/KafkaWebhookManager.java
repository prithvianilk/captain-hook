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

        CreateTopicsResult result = adminClient.createTopics(newTopics);
        result.all();
    }

    @Override
    public Collection<String> getEventTypes() {
        try {
            return adminClient.listTopics().names().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Properties getProperties() {
        try {
            Properties config = new Properties();
            config.put(CommonClientConfigs.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
            config.put(CommonClientConfigs.GROUP_ID_CONFIG, "foo");
            config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);
            config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
            return config;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
