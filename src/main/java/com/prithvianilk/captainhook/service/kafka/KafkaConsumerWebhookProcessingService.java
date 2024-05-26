package com.prithvianilk.captainhook.service.kafka;

import com.prithvianilk.captainhook.constant.KafkaConstants;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.domain.HttpCommand;
import com.prithvianilk.captainhook.domain.WebhookEvent;
import com.prithvianilk.captainhook.service.WebhookProcessingException;
import com.prithvianilk.captainhook.service.WebhookProcessingService;
import com.prithvianilk.captainhook.service.http.WebhookHttpClient;
import com.prithvianilk.captainhook.serializer.kafka.JacksonObjectMapperKafkaWebhookEventValueDeserializer;
import com.prithvianilk.captainhook.service.retry.WebhookHttpRetryer;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumerWebhookProcessingService extends WebhookProcessingService {
    KafkaConsumer<String, WebhookEvent> kafkaConsumer;

    WebhookHttpRetryer webhookHttpRetryer;

    Duration consumerPollDuration;

    public KafkaConsumerWebhookProcessingService(EventType eventType, Duration consumerPollDuration) {
        super(eventType);
        webhookHttpRetryer = new WebhookHttpRetryer(new WebhookHttpClient());

        Properties config = getProperties();
        kafkaConsumer = new KafkaConsumer<>(config);
        kafkaConsumer.subscribe(Collections.singleton(eventType.id()));

        this.consumerPollDuration = consumerPollDuration;
    }

    private Properties getProperties() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.LOCAL_KAFKA_BOOTSTRAP_SERVER);

        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "webhook_consumer" + UUID.randomUUID());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer_group");
        config.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, eventType.id());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonObjectMapperKafkaWebhookEventValueDeserializer.class);

        return config;
    }

    @Override
    public WebhookConsumptionResult consumeAndProcessWebhook() {
        log.info("Polling for webhook for event_type: {}...", eventType.id());
        ConsumerRecords<String, WebhookEvent> consumerRecords = kafkaConsumer.poll(consumerPollDuration);

        for (ConsumerRecord<String, WebhookEvent> consumerRecord : consumerRecords) {
            try {
                WebhookEvent webhookEvent = handleConsumerRecord(consumerRecord);
                return new WebhookConsumptionResult(Optional.of(webhookEvent), Optional.empty());
            } catch (WebhookProcessingException e) {
                return new WebhookConsumptionResult(Optional.empty(), Optional.of(e.getWebhookEvent()));
            } finally {
                kafkaConsumer.commitSync();
            }
        }

        kafkaConsumer.commitSync();

        return new WebhookConsumptionResult(Optional.empty(), Optional.empty());
    }

    private WebhookEvent handleConsumerRecord(ConsumerRecord<String, WebhookEvent> consumerRecord) throws WebhookProcessingException {
        log.info("Handling offset: {} for event_type: {}", consumerRecord.offset(), eventType.id());
        WebhookEvent webhookEvent = consumerRecord.value();

        try {
            switch (webhookEvent.command()) {
                case HttpCommand httpCommand -> {
                    webhookHttpRetryer.attemptWithRetry(httpCommand, eventType.retryConfig());
                }
            }
            return webhookEvent;
        } catch (Exception e) {
            log.error("Webhook processing failed for: {}", consumerRecord.offset(), e);
            throw new WebhookProcessingException(webhookEvent);
        }
    }
}
