package org.example.webhook.service;

import org.example.webhook.domain.event.EventType;

import java.util.Set;

public interface WebhookEventTypeManager {
    void registerNewEventType(EventType eventType);

    Set<String> getRegisteredEventTypes();
}
