package org.example.webhook.repository;

import org.example.webhook.entity.EventTypeEntity;

import java.util.List;

public interface EventTypeRepository {
    List<EventTypeEntity> findAllEventTypes();

    EventTypeEntity save(EventTypeEntity eventType);
}
