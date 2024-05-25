package org.example.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.Command;
import org.example.webhook.domain.WebhookEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "webhook_events")
public class WebhookEventEntity {
    @Id
    String id;

    @Column
    String eventType;

    @Column
    @Convert(converter = CommandToStringConverter.class)
    Command command;

    @Column
    WebhookEvent.Status status;
}
