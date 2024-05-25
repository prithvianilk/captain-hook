package org.example.webhook.service;

import org.example.webhook.domain.event.WebhookEvent;
import org.example.webhook.entity.WebhookEventEntity;
import org.example.webhook.mapper.WebhookEntityMapper;
import org.example.webhook.repository.WebhookRepository;

import java.util.List;

public class WebhookService {
    WebhookRepository webhookRepository;

    WebhookCreationClient webhookCreationClient;

    public WebhookEvent createWebhook(WebhookEvent webhookEvent) throws WebhookCreationException {
        WebhookEventEntity webhookEventEntity = WebhookEntityMapper.toEntity(webhookEvent, WebhookEvent.Status.CREATED);
        webhookRepository.save(webhookEventEntity);
        webhookCreationClient.publish(webhookEvent.eventType(), webhookEvent);
        return webhookEvent;
    }

    public List<WebhookEvent> getAllWebhooksByEventType(String eventType) {
        return webhookRepository
                .findAllByEventType(eventType)
                .stream()
                .map(WebhookEntityMapper::toDomain)
                .toList();
    }
}
