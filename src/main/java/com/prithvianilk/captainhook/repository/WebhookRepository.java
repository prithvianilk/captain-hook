package com.prithvianilk.captainhook.repository;

import com.prithvianilk.captainhook.entity.WebhookEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebhookRepository extends JpaRepository<WebhookEventEntity, String> {
    List<WebhookEventEntity> findAllByEventType(String eventType);

    boolean existsByIdAndEventType(String id, String eventType);
}
