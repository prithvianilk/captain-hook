package com.prithvianilk.captainhook.service.kafka;

import com.prithvianilk.captainhook.service.WebhookEventTypeManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import com.prithvianilk.captainhook.domain.EventType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaWebhookEventTypeManager implements WebhookEventTypeManager {
    AdminClient adminClient;

    NewEventTypeKafkaProducer newEventTypeKafkaProducer;

    public KafkaWebhookEventTypeManager(NewEventTypeKafkaProducer newEventTypeKafkaProducer) {
        Properties config = getProperties();
        adminClient = AdminClient.create(config);
        this.newEventTypeKafkaProducer = newEventTypeKafkaProducer;
    }

    @Override
    public void registerNewEventType(EventType eventType) {
        NewTopic newTopic = new NewTopic(eventType.id(), 1, (short) 1);
        adminClient.createTopics(Collections.singletonList(newTopic));
        newEventTypeKafkaProducer.publishNewEvent(eventType);
    }

    @Override
    public Set<String> getRegisteredEventTypes() {
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
