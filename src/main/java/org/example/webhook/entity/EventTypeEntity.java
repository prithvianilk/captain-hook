package org.example.webhook.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.event.RetryConfig;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventTypeEntity {
    String id;

    RetryConfig retryConfig;
}
