package com.prithvianilk.captainhook.controller;

import com.prithvianilk.captainhook.domain.EventType;
import com.prithvianilk.captainhook.dto.CreateEventTypeRequest;
import com.prithvianilk.captainhook.service.EventTypeAlreadyExistsException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.prithvianilk.captainhook.service.EventTypeService;
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
        EventType eventType = new EventType(request.id(), request.retryConfig());
        try {
            return ResponseEntity.ok(eventTypeService.createEventType(eventType));
        } catch (EventTypeAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
