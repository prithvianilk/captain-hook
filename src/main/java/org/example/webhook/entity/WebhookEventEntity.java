package org.example.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.Command;
import org.example.webhook.domain.WebhookEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "webhook_events")
@Table(name = "webhook_events", uniqueConstraints = {
        @UniqueConstraint(name = "id_event_type_id", columnNames = {"id", "eventType"})
})
public class WebhookEventEntity {
    @Id
    @Column(nullable = false)
    String id;

    @Column(nullable = false)
    String eventType;

    @Column(nullable = false)
    @Convert(converter = CommandToStringConverter.class)
    Command command;

    @Column(nullable = false)
    WebhookEvent.Status status;
}
