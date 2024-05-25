package com.prithvianilk.captainhook.service;

import com.prithvianilk.captainhook.domain.WebhookEvent;
import com.prithvianilk.captainhook.mapper.WebhookEntityMapper;
import com.prithvianilk.captainhook.service.kafka.KafkaConsumerWebhookProcessingService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.dto.NewEventTypeAddedEvent;
import com.prithvianilk.captainhook.entity.EventTypeEntity;
import com.prithvianilk.captainhook.mapper.EventTypeEntityMapper;
import com.prithvianilk.captainhook.repository.EventTypeRepository;
import com.prithvianilk.captainhook.repository.WebhookRepository;
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
