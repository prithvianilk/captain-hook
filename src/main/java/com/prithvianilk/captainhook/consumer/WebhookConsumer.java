package com.prithvianilk.captainhook.consumer;

import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.domain.WebhookEvent;
import com.prithvianilk.captainhook.dto.NewEventTypeDiscoveredEvent;
import com.prithvianilk.captainhook.mapper.EventTypeEntityMapper;
import com.prithvianilk.captainhook.mapper.WebhookEntityMapper;
import com.prithvianilk.captainhook.repository.EventTypeRepository;
import com.prithvianilk.captainhook.repository.WebhookRepository;
import com.prithvianilk.captainhook.service.WebhookProcessingService;
import com.prithvianilk.captainhook.service.kafka.KafkaConsumerWebhookProcessingService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebhookConsumer {
    final EventTypeRepository eventTypeRepository;

    final WebhookRepository webhookRepository;

    final ExecutorService executorService;

    final Duration webhookProcessorMaxAcceptableLag;

    Set<EventType> eventTypes;

    public WebhookConsumer(
            EventTypeRepository eventTypeRepository,
            WebhookRepository webhookRepository,
            @Value("${webhook-processor.max-acceptable-lag}") Duration webhookProcessorMaxAcceptableLag) {
        this.eventTypeRepository = eventTypeRepository;
        this.webhookRepository = webhookRepository;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.webhookProcessorMaxAcceptableLag = webhookProcessorMaxAcceptableLag;
    }

    @EventListener(value = NewEventTypeDiscoveredEvent.class)
    public void onNewEventTypeDiscoveredEvent(NewEventTypeDiscoveredEvent event) {
        if (eventTypes.contains(event.getEventType())) {
            return;
        }

        log.info("Found new event type: {}", event.getEventType().id());
        eventTypes.add(event.getEventType());
        executorService.submit(() -> startConsumptionForEventType(event.getEventType()));
    }

    @PostConstruct
    public void startConsumption() {
        eventTypes = eventTypeRepository
                .findAll()
                .stream()
                .map(EventTypeEntityMapper::toDomain)
                .collect(Collectors.toSet());

        eventTypes.forEach(eventType -> {
            executorService.submit(() -> startConsumptionForEventType(eventType));
        });
    }

    private void startConsumptionForEventType(EventType eventType) {
        log.info("Starting consumption for: {}", eventType.id());

        WebhookProcessingService webhookProcessingService = new KafkaConsumerWebhookProcessingService(eventType, webhookProcessorMaxAcceptableLag);

        while (true) {
            WebhookProcessingService.WebhookConsumptionResult result = webhookProcessingService.consumeAndProcessWebhook();

            result.succeededWebhookEvent()
                    .map(webhookEvent -> WebhookEntityMapper.toEntity(webhookEvent, WebhookEvent.Status.PROCESSED))
                    .ifPresent(webhookRepository::save);

            result.failedWebhookEvent()
                    .map(webhookEvent -> WebhookEntityMapper.toEntity(webhookEvent, WebhookEvent.Status.FAILED))
                    .ifPresent(webhookRepository::save);
        }
    }
}
