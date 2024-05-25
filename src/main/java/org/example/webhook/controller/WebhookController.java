package org.example.webhook.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.WebhookEvent;
import org.example.webhook.dto.CreateWebhookEventRequest;
import org.example.webhook.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/webhook/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebhookController {
    WebhookService webhookService;

    @GetMapping("/{eventType}")
    public ResponseEntity<List<WebhookEvent>> getAllWebhookEventsByEventType(@PathVariable("eventType") String eventType) {
        return ResponseEntity.ok(webhookService.getAllWebhooksByEventType(eventType));
    }

    @PostMapping
    public ResponseEntity<WebhookEvent> create(@RequestBody CreateWebhookEventRequest request) {
        WebhookEvent webhookEvent = new WebhookEvent(request.id(), request.eventType(), request.command(), null);
        return ResponseEntity.ok(webhookService.createWebhook(webhookEvent));
    }
}
