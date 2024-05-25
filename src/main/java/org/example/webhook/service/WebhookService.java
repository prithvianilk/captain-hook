package org.example.webhook.service;

import org.example.webhook.domain.event.WebhookEvent;
import org.example.webhook.entity.WebhookEventEntity;
import org.example.webhook.mapper.WebhookEntityMapper;
import org.example.webhook.repository.WebhookRepository;

public class WebhookService {
    WebhookRepository webhookRepository;

    WebhookCreationClient webhookCreationClient;

    public WebhookEvent createWebhook(WebhookEvent webhookEvent) throws WebhookCreationException {
        WebhookEventEntity webhookEventEntity = WebhookEntityMapper.toEntity(webhookEvent, WebhookEventEntity.Status.CREATED);
        webhookRepository.save(webhookEventEntity);
        webhookCreationClient.publish(webhookEvent.eventType(), webhookEvent);
        return webhookEvent;
    }
}
