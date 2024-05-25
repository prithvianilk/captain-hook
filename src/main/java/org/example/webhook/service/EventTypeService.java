package org.example.webhook.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.EventType;
import org.example.webhook.dto.NewEventTypeAddedEvent;
import org.example.webhook.entity.EventTypeEntity;
import org.example.webhook.mapper.EventTypeEntityMapper;
import org.example.webhook.repository.EventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventTypeService {
    EventTypeRepository eventTypeRepository;

    WebhookEventTypeManager webhookEventTypeManager;

    ApplicationEventPublisher applicationEventPublisher;

    public List<EventType> getAllEvents() {
        return eventTypeRepository
                .findAll()
                .stream()
                .map(EventTypeEntityMapper::toDomain)
                .toList();
    }

    public EventType createEventType(EventType eventType) throws EventTypeAlreadyExistsException {
        if (eventTypeRepository.existsById(eventType.id())) {
            throw new EventTypeAlreadyExistsException();
        }

        EventTypeEntity eventTypeEntity = EventTypeEntityMapper.toEntity(eventType);
        eventTypeRepository.save(eventTypeEntity);

        webhookEventTypeManager.registerNewEventType(eventType);
        applicationEventPublisher.publishEvent(new NewEventTypeAddedEvent(this, eventType.id()));

        return eventType;
    }
}
