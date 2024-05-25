package org.example.webhook;

import java.util.Collection;
import java.util.List;

public interface WebhookManager {
    void registerNewEventTypes(List<String> eventTypes);

    Collection<String> getEventTypes();
}
