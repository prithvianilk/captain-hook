package org.example.webhook.service;

import org.example.webhook.domain.event.EventType;
import org.example.webhook.entity.EventTypeEntity;
import org.example.webhook.entity.WebhookEventEntity;
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
                .map(eventTypeEntity -> new EventType(eventTypeEntity.getId(), eventTypeEntity.getRetryConfig()))
                .forEach(eventType -> {
                    executorService.submit(() -> startConsumptionForEventType(eventType));
                });
    }

    private void startConsumptionForEventType(EventType eventType) {
        WebhookProcessingService webhookProcessingService = new KafkaConsumerWebhookProcessingService(eventType);

        while (true) {
            webhookProcessingService
                    .pollAndConsume()
                    .map(webhookEvent -> WebhookEntityMapper.toEntity(webhookEvent, WebhookEventEntity.Status.PROCESSED))
                    .ifPresent(webhookRepository::save);
        }
    }
}
