package com.prithvianilk.captainhook.service;

import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.dto.NewEventTypeAddedEvent;
import com.prithvianilk.captainhook.entity.EventTypeEntity;
import com.prithvianilk.captainhook.mapper.EventTypeEntityMapper;
import com.prithvianilk.captainhook.repository.EventTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
