package com.prithvianilk.captainhook.dto;

import com.prithvianilk.captainhook.domain.Command;

public record CreateWebhookEventRequest(String id, String eventType, Command command) {
}
