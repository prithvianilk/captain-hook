package org.example.webhook.event;

public record WebhookEvent(String eventId, Command command, RetryConfig retryConfig) {
}
