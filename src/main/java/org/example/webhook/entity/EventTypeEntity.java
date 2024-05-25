package org.example.webhook.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.RetryConfig;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "event_types")
public class EventTypeEntity {
    @Id
    String id;

    @Column(nullable = false)
    @Convert(converter = RetryConfigToStringConverter.class)
    RetryConfig retryConfig;
}
