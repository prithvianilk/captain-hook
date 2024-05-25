package org.example.webhook.service;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.webhook.domain.EventType;
import org.example.webhook.domain.WebhookEvent;
import org.example.webhook.dto.NewEventTypeAddedEvent;
import org.example.webhook.entity.EventTypeEntity;
import org.example.webhook.mapper.EventTypeEntityMapper;
import org.example.webhook.mapper.WebhookEntityMapper;
import org.example.webhook.repository.EventTypeRepository;
import org.example.webhook.repository.WebhookRepository;
import org.example.webhook.service.kafka.KafkaConsumerWebhookProcessingService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebhookConsumptionService {
    final EventTypeRepository eventTypeRepository;

    final WebhookRepository webhookRepository;

    ExecutorService executorService;

    public WebhookConsumptionService(EventTypeRepository eventTypeRepository, WebhookRepository webhookRepository) {
        this.eventTypeRepository = eventTypeRepository;
        this.webhookRepository = webhookRepository;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @EventListener(value = NewEventTypeAddedEvent.class)
    public void listen(NewEventTypeAddedEvent event) {
        log.info("Restarting consumers due to new event_type addition: {}", event.getEventType());

        executorService.shutdownNow();
        executorService = Executors.newVirtualThreadPerTaskExecutor();

        startConsumption();
    }

    @PostConstruct
    public void startConsumption() {
        List<EventTypeEntity> eventTypeEntities = eventTypeRepository.findAll();

        eventTypeEntities
                .stream()
                .map(EventTypeEntityMapper::toDomain)
                .forEach(eventType -> {
                    executorService.submit(() -> startConsumptionForEventType(eventType));
                });
    }

    private void startConsumptionForEventType(EventType eventType) {
        log.info("Starting consumption for: {}", eventType);

        WebhookProcessingService webhookProcessingService = new KafkaConsumerWebhookProcessingService(eventType);
        webhookProcessingService.start();

        while (true) {
            WebhookProcessingService.WebhookConsumptionResult result = webhookProcessingService.pollAndConsume();

            result.succeededWebhookEvent()
                    .map(webhookEvent -> WebhookEntityMapper.toEntity(webhookEvent, WebhookEvent.Status.PROCESSED))
                    .ifPresent(webhookRepository::save);

            result.failedWebhookEvent()
                    .map(webhookEvent -> WebhookEntityMapper.toEntity(webhookEvent, WebhookEvent.Status.FAILED))
                    .ifPresent(webhookRepository::save);
        }
    }
}
