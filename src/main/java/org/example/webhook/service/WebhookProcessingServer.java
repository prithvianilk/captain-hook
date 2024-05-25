package org.example.webhook.service;

import java.util.List;

public abstract class WebhookProcessingServer {
    private final List<String> eventTypes;

    protected WebhookProcessingServer(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    protected List<String> getEventTypes() {
        return eventTypes;
    }

    public abstract void start();
}
