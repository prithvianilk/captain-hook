package org.example.webhook.repository;

import org.example.webhook.entity.WebhookEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebhookRepository extends JpaRepository<WebhookEventEntity, String> {
    List<WebhookEventEntity> findAllByEventType(String eventType);
}
