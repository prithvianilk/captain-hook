package com.prithvianilk.captainhook.service.kafka;

import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.service.kafka.serialization.JacksonObjectMapperKafkaEventTypeValueSerializer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewEventTypeKafkaProducer {
    KafkaProducer<String, EventType> kafkaProducer;

    public NewEventTypeKafkaProducer() {
        Properties config = getProperties();
        kafkaProducer = new KafkaProducer<>(config);
    }

    private Properties getProperties() {
        Properties config = new Properties();
        config.put(CommonClientConfigs.CLIENT_ID_CONFIG, "event_type_producer:" + UUID.randomUUID());
        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaEventTypeValueSerializer.class);
        return config;
    }

    public void publishNewEvent(EventType eventType) {
        kafkaProducer.send(new ProducerRecord<>(KafkaConstants.NEW_EVENT_TYPE_ADDED_TOPIC_NAME, eventType.id(), eventType));
    }
}
