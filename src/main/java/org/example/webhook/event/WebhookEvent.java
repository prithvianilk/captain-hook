package org.example.webhook.event;

public record WebhookEvent(Command command, RetryConfig retryConfig) {
}
