package org.example.webhook.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.RetryConfig;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CreateEventTypeRequest {
    String id;

    RetryConfig retryConfig = RetryConfig.singleAttempt();
}
