package org.example.webhook.service;

import org.example.webhook.domain.EventType;
import org.example.webhook.domain.WebhookEvent;

import java.util.Optional;

public abstract class WebhookProcessingService {
    protected final EventType eventType;

    protected WebhookProcessingService(EventType eventType) {
        this.eventType = eventType;
    }

    public abstract void start();

    public abstract WebhookConsumptionResult pollAndConsume();

    public record WebhookConsumptionResult(
            Optional<WebhookEvent> succeededWebhookEvent,
            Optional<WebhookEvent> failedWebhookEvent) {
    }
}
