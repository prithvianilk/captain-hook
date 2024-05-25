package org.example.webhook.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public record RetryConfig(
        List<Integer> successStatusCode,
        Integer maxAttemptCount,
        AttemptBackoffConfig attemptBackoffConfig) {

    public static RetryConfig singleAttempt() {
        return new RetryConfig(Collections.emptyList(), 1, new NoOpBackoffConfig());
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(name = "NOOP", value = NoOpBackoffConfig.class),
            @JsonSubTypes.Type(name = "CONSTANT", value = ConstantBackoffConfig.class)
    })
    public interface AttemptBackoffConfig {
        Duration getWaitTime(int attemptCount);
    }

    record NoOpBackoffConfig() implements AttemptBackoffConfig {
        @Override
        public Duration getWaitTime(int attemptCount) {
            return Duration.ZERO;
        }
    }

    record ConstantBackoffConfig(Duration waitTime) implements AttemptBackoffConfig {
        @Override
        public Duration getWaitTime(int attemptCount) {
            return waitTime;
        }
    }
}
