package org.example.webhook.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.event.Command;

@Data
@Builder
@FieldDefaults
public class WebhookEventEntity {
    String id;

    Command command;

    Status status;

    public enum Status {
        CREATED, PROCESSED, FAILED
    }
}
