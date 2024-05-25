package org.example.webhook.domain.event;

public record WebhookEvent(String eventId, Command command, RetryConfig retryConfig) {
}
