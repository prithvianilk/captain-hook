package org.example.webhook.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.event.Command;
import org.example.webhook.domain.event.WebhookEvent;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebhookEventEntity {
    String id;

    String eventType;

    Command command;

    WebhookEvent.Status status;
}
