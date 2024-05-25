package org.example.webhook.service;

import org.example.webhook.domain.event.EventType;
import org.example.webhook.domain.event.WebhookEvent;
import org.example.webhook.entity.EventTypeEntity;
import org.example.webhook.mapper.EventTypeEntityMapper;
import org.example.webhook.mapper.WebhookEntityMapper;
import org.example.webhook.repository.EventTypeRepository;
import org.example.webhook.repository.WebhookRepository;
import org.example.webhook.service.kafka.KafkaConsumerWebhookProcessingService;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class WebhookConsumptionService {
    EventTypeRepository eventTypeRepository;

    WebhookRepository webhookRepository;

    ExecutorService executorService;

    public WebhookConsumptionService(
            EventTypeRepository eventTypeRepository,
            WebhookRepository webhookRepository,
            ExecutorService executorService) {
        this.eventTypeRepository = eventTypeRepository;
        this.webhookRepository = webhookRepository;
        this.executorService = executorService;
    }

    public void startConsumption() {
        List<EventTypeEntity> eventTypeEntities = eventTypeRepository.findAllEventTypes();

        eventTypeEntities
                .stream()
                .map(EventTypeEntityMapper::toDomain)
                .forEach(eventType -> {
                    executorService.submit(() -> startConsumptionForEventType(eventType));
                });
    }

    private void startConsumptionForEventType(EventType eventType) {
        System.out.println("Starting consumption for: " + eventType);

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
