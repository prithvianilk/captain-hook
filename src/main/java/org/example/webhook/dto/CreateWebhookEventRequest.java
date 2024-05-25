package org.example.webhook.dto;

import org.example.webhook.domain.Command;

public record CreateWebhookEventRequest(String id, String eventType, Command command) {
}
