package org.example.webhook.domain;

public record EventType(String id, RetryConfig retryConfig) {
}
