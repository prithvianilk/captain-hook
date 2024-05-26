package com.prithvianilk.captainhook.service;

import com.prithvianilk.captainhook.domain.WebhookEvent;
import com.prithvianilk.captainhook.entity.WebhookEventEntity;
import com.prithvianilk.captainhook.mapper.WebhookEntityMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.prithvianilk.captainhook.repository.WebhookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebhookService {
    WebhookRepository webhookRepository;

    WebhookCreationClient webhookCreationClient;

    public WebhookEvent createWebhook(WebhookEvent webhookEvent) throws WebhookEventAlreadyExistsException {
        if (webhookRepository.existsByIdAndEventType(webhookEvent.id(), webhookEvent.eventType())) {
            throw new WebhookEventAlreadyExistsException();
        }

        WebhookEventEntity webhookEventEntity = WebhookEntityMapper.toEntity(webhookEvent, WebhookEvent.Status.CREATED);
        webhookRepository.save(webhookEventEntity);
        webhookCreationClient.publish(webhookEvent.eventType(), webhookEvent);
        return webhookEvent;
    }

    public List<WebhookEvent> getAllWebhooksByEventType(String eventType, Optional<WebhookEvent.Status> optionalStatus) {
        List<WebhookEventEntity> entities = optionalStatus
                .map(status -> webhookRepository.findAllByEventTypeAndStatus(eventType, status))
                .orElseGet(() -> webhookRepository.findAllByEventType(eventType));

        return entities
                .stream()
                .map(WebhookEntityMapper::toDomain)
                .toList();
    }
}
