package org.example.webhook.service;

import org.example.webhook.domain.event.EventType;
import org.example.webhook.entity.EventTypeEntity;
import org.example.webhook.mapper.EventTypeEntityMapper;
import org.example.webhook.repository.EventTypeRepository;

import java.util.List;

public class EventTypeService {
    private EventTypeRepository eventTypeRepository;

    private WebhookEventTypeManager webhookEventTypeManager;

    public List<EventType> getAllEvents() {
        return eventTypeRepository
                .findAllEventTypes()
                .stream()
                .map(EventTypeEntityMapper::toDomain)
                .toList();
    }

    public EventType createEventType(EventType eventType) throws EventTypeCreationException {
        EventTypeEntity eventTypeEntity = EventTypeEntityMapper.toEntity(eventType);
        eventTypeRepository.save(eventTypeEntity);
        webhookEventTypeManager.registerNewEventType(eventType);
        return eventType;
    }
}
