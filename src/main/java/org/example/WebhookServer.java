package org.example;

import java.util.List;

public abstract class WebhookServer {
    private final List<String> eventTypes;

    protected WebhookServer(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    protected List<String> getEventTypes() {
        return eventTypes;
    }

    protected abstract void start();
}