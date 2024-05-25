package org.example.webhook.domain.event;

public record EventType(String id, RetryConfig retryConfig) {
}
