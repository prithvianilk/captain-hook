package org.example.webhook.service;

import java.util.Collection;
import java.util.List;

public interface WebhookManager {
    void registerNewEventTypes(List<String> eventTypes);

    Collection<String> getRegisteredEventTypes();
}
