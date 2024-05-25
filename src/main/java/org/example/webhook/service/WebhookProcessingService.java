package org.example.webhook.service;

import org.example.webhook.domain.event.EventType;
import org.example.webhook.domain.event.WebhookEvent;

import java.util.Optional;

public abstract class WebhookProcessingService {
    protected final EventType eventType;

    protected WebhookProcessingService(EventType eventType) {
        this.eventType = eventType;
    }

    public abstract void start();

    public abstract Optional<WebhookEvent> pollAndConsume() throws WebhookProcessingException;
}
