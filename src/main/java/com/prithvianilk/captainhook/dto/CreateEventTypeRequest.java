package com.prithvianilk.captainhook.dto;

import com.prithvianilk.captainhook.domain.RetryConfig;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

public record CreateEventTypeRequest(String id, RetryConfig retryConfig) {
    @Override
    public RetryConfig retryConfig() {
        return Optional.ofNullable(retryConfig).orElseGet(RetryConfig::singleAttempt);
    }
}
