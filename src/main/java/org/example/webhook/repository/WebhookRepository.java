package org.example.webhook.repository;

import org.example.webhook.entity.WebhookEventEntity;

public interface WebhookRepository {
    void save(WebhookEventEntity webhookEvent);
}
