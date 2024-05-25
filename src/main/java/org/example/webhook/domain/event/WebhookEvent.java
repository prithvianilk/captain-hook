package org.example.webhook.domain.event;

public record WebhookEvent(String id, String eventType, Command command) {
}
