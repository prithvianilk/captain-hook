package com.prithvianilk.captainhook.dto;

import com.prithvianilk.captainhook.domain.RetryConfig;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CreateEventTypeRequest {
    String id;

    RetryConfig retryConfig = RetryConfig.singleAttempt();
}
