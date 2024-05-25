package org.example.webhook.event;

public record WebhookEvent(HttpCommand httpCommand, RetryConfig retryConfig) {
}
