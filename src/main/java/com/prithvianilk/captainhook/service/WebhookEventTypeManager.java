package com.prithvianilk.captainhook.service;

import com.prithvianilk.captainhook.domain.EventType;

import java.util.Set;

public interface WebhookEventTypeManager {
    void registerNewEventType(EventType eventType);

    Set<String> getRegisteredEventTypes();
}
