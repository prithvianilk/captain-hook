package org.example.webhook.mapper;

import lombok.experimental.UtilityClass;
import org.example.webhook.domain.WebhookEvent;
import org.example.webhook.entity.WebhookEventEntity;

@UtilityClass
public class WebhookEntityMapper {
    public static WebhookEventEntity toEntity(WebhookEvent webhookEvent, WebhookEvent.Status status) {
        return WebhookEventEntity
                .builder()
                .id(webhookEvent.id())
                .eventType(webhookEvent.eventType())
                .command(webhookEvent.command())
                .status(status)
                .build();
    }

    public static WebhookEvent toDomain(WebhookEventEntity entity) {
        return new WebhookEvent(entity.getId(), entity.getEventType(), entity.getCommand(), entity.getStatus());
    }
}
