package com.prithvianilk.captainhook.service.kafka;

import com.prithvianilk.captainhook.constant.KafkaConstants;
import com.prithvianilk.captainhook.service.WebhookEventTypeManager;
import com.prithvianilk.captainhook.serializer.kafka.JacksonObjectMapperKafkaEventTypeValueSerializer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import com.prithvianilk.captainhook.domain.EventType;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
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

    KafkaProducer<String, EventType> kafkaProducer;

    public KafkaWebhookEventTypeManager() {
        Properties adminClientConfig = getAdminClientProperties();
        adminClient = AdminClient.create(adminClientConfig);

        Properties newEventTypeProducerConfig = getNewEventTypeProducerConfig();
        kafkaProducer = new KafkaProducer<>(newEventTypeProducerConfig);
    }

    @Override
    public void registerNewEventType(EventType eventType) {
        NewTopic newTopic = new NewTopic(eventType.id(), 1, (short) 1);
        adminClient.createTopics(Collections.singletonList(newTopic));
        kafkaProducer.send(new ProducerRecord<>(KafkaConstants.NEW_EVENT_TYPE_ADDED_TOPIC_NAME, eventType.id(), eventType));
    }

    @Override
    public Set<String> getRegisteredEventTypes() {
        try {
            return adminClient.listTopics().names().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Properties getAdminClientProperties() {
        Properties config = new Properties();
        config.put(CommonClientConfigs.CLIENT_ID_CONFIG, "webhook_manager:" + UUID.randomUUID());
        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);
        return config;
    }

    private Properties getNewEventTypeProducerConfig() {
        Properties config = new Properties();
        config.put(CommonClientConfigs.CLIENT_ID_CONFIG, "event_type_producer:" + UUID.randomUUID());
        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaEventTypeValueSerializer.class);
        return config;
    }
}
