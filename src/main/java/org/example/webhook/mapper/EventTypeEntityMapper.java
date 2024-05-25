package org.example.webhook.mapper;

import lombok.experimental.UtilityClass;
import org.example.webhook.domain.event.EventType;
import org.example.webhook.entity.EventTypeEntity;

@UtilityClass
public class EventTypeEntityMapper {
    public static EventTypeEntity toEntity(EventType eventType) {
        return EventTypeEntity
                .builder()
                .id(eventType.id())
                .retryConfig(eventType.retryConfig())
                .build();
    }

    public static EventType toDomain(EventTypeEntity eventTypeEntity) {
        return new EventType(eventTypeEntity.getId(), eventTypeEntity.getRetryConfig());
    }
}
