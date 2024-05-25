package org.example.webhook.repository;

import org.example.webhook.entity.EventTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventTypeEntity, String> {
}
