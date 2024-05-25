package com.prithvianilk.captainhook.entity;

import com.prithvianilk.captainhook.domain.RetryConfig;
import com.prithvianilk.captainhook.entity.converter.RetryConfigToStringConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
