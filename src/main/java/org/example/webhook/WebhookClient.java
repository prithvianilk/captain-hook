package org.example.webhook;

import org.example.webhook.event.WebhookEvent;

public interface WebhookClient {
    void publish(String eventType, WebhookEvent webhookEvent);
}
