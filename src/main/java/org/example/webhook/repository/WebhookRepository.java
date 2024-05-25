package org.example.webhook.repository;

import org.example.webhook.domain.event.WebhookEvent;
import org.example.webhook.entity.WebhookEventEntity;

import java.util.Collection;
import java.util.List;

public interface WebhookRepository {
    void save(WebhookEventEntity webhookEvent);

    List<WebhookEventEntity> findAllByEventType(String eventType);
}
