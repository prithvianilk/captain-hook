package com.prithvianilk.captainhook.mapper;

import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.entity.EventTypeEntity;
import lombok.experimental.UtilityClass;

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
