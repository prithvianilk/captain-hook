package com.prithvianilk.captainhook.dto;

import com.prithvianilk.captainhook.domain.EventType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewEventTypeDiscoveredEvent extends ApplicationEvent {
    EventType eventType;

    public NewEventTypeDiscoveredEvent(Object source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }
}
