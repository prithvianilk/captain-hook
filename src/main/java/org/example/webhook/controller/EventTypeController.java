package org.example.webhook.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.EventType;
import org.example.webhook.dto.CreateEventTypeRequest;
import org.example.webhook.service.EventTypeAlreadyExistsException;
import org.example.webhook.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventTypeController {
    EventTypeService eventTypeService;

    @GetMapping
    public ResponseEntity<List<EventType>> getAllEventTypes() {
        return ResponseEntity.ok(eventTypeService.getAllEvents());
    }

    @PostMapping
    public ResponseEntity<EventType> create(@RequestBody CreateEventTypeRequest request) {
        EventType eventType = new EventType(request.getId(), request.getRetryConfig());

        try {
            return ResponseEntity.ok(eventTypeService.createEventType(eventType));
        } catch (EventTypeAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
