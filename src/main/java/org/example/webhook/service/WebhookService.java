package org.example.webhook.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.WebhookEvent;
import org.example.webhook.entity.WebhookEventEntity;
import org.example.webhook.mapper.WebhookEntityMapper;
import org.example.webhook.repository.WebhookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebhookService {
    WebhookRepository webhookRepository;

    WebhookCreationClient webhookCreationClient;

    public WebhookEvent createWebhook(WebhookEvent webhookEvent) throws WebhookCreationException {
        WebhookEventEntity webhookEventEntity = WebhookEntityMapper.toEntity(webhookEvent, WebhookEvent.Status.CREATED);
        webhookRepository.save(webhookEventEntity);
        webhookCreationClient.publish(webhookEvent.eventType(), webhookEvent);
        return webhookEvent;
    }

    public List<WebhookEvent> getAllWebhooksByEventType(String eventType) {
        return webhookRepository
                .findAllByEventType(eventType)
                .stream()
                .map(WebhookEntityMapper::toDomain)
                .toList();
    }
}
