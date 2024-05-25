package org.example.webhook.mapper;

import lombok.experimental.UtilityClass;
import org.example.webhook.domain.event.WebhookEvent;
import org.example.webhook.entity.WebhookEventEntity;

@UtilityClass
public class WebhookEntityMapper {
    public static WebhookEventEntity toEntity(WebhookEvent webhookEvent, WebhookEventEntity.Status status) {
        return WebhookEventEntity
                .builder()
                .id(webhookEvent.id())
                .command(webhookEvent.command())
                .status(status)
                .build();
    }
}
