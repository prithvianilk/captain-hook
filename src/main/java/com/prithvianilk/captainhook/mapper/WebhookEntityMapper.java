package com.prithvianilk.captainhook.mapper;

import lombok.experimental.UtilityClass;
import com.prithvianilk.captainhook.domain.WebhookEvent;
import com.prithvianilk.captainhook.entity.WebhookEventEntity;

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
