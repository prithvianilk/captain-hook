package org.example.webhook.service;

import org.example.webhook.domain.WebhookEvent;

public interface WebhookCreationClient {
    void publish(String eventType, WebhookEvent webhookEvent);
}
