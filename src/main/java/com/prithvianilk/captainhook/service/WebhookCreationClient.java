package com.prithvianilk.captainhook.service;

import com.prithvianilk.captainhook.domain.WebhookEvent;

public interface WebhookCreationClient {
    void publish(String eventType, WebhookEvent webhookEvent);
}
