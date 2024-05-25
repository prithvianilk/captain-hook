package org.example.webhook.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewEventTypeAddedEvent extends ApplicationEvent {
    String eventType;

    public NewEventTypeAddedEvent(Object source, String eventType) {
        super(source);
        this.eventType = eventType;
    }
}
