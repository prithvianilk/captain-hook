package com.prithvianilk.captainhook.consumer.kafka;

import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.dto.NewEventTypeDiscoveredEvent;
import com.prithvianilk.captainhook.constant.KafkaConstants;
import com.prithvianilk.captainhook.serializer.kafka.JacksonObjectMapperKafkaEventTypeValueDeserializer;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewEventTypeKafkaConsumer {
    KafkaConsumer<String, EventType> eventTypeKafkaConsumer;

    ApplicationEventPublisher applicationEventPublisher;

    ExecutorService executorService;

    Duration pollTimeout;

    public NewEventTypeKafkaConsumer(
            ApplicationEventPublisher applicationEventPublisher,
            @Value("${new-event-type.max-acceptable-lag}") Duration maxAcceptableLag) {
        Properties properties = getProperties();
        eventTypeKafkaConsumer = new KafkaConsumer<>(properties);
        this.applicationEventPublisher = applicationEventPublisher;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.pollTimeout = maxAcceptableLag;
    }

    private Properties getProperties() {
        String instanceId = UUID.randomUUID().toString();

        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);

        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "new_event_type_consumer" + instanceId);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, instanceId);
        config.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, instanceId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaEventTypeValueDeserializer.class);
        return config;
    }

    @PostConstruct
    void startNewEventTypeListening() {
        eventTypeKafkaConsumer.subscribe(Collections.singletonList(KafkaConstants.NEW_EVENT_TYPE_ADDED_TOPIC_NAME));

        executorService.submit(() -> {
            while (true) {
                log.info("Polling for new event_types...");
                ConsumerRecords<String, EventType> records = eventTypeKafkaConsumer.poll(pollTimeout);

                records.forEach(record -> {
                    EventType eventType = record.value();
                    applicationEventPublisher.publishEvent(new NewEventTypeDiscoveredEvent(this, eventType));
                });

                eventTypeKafkaConsumer.commitSync();
            }
        });
    }
}
