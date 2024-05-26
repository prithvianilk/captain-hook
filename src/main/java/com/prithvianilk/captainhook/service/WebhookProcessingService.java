package com.prithvianilk.captainhook.service;

import com.prithvianilk.captainhook.domain.WebhookEvent;
import com.prithvianilk.captainhook.domain.EventType;

import java.util.Optional;

public abstract class WebhookProcessingService {
    protected final EventType eventType;

    protected WebhookProcessingService(EventType eventType) {
        this.eventType = eventType;
    }

    public abstract WebhookConsumptionResult consumeAndProcessWebhook();

    public record WebhookConsumptionResult(
            Optional<WebhookEvent> succeededWebhookEvent,
            Optional<WebhookEvent> failedWebhookEvent) {
    }
}
