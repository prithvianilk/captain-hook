package org.example.webhook.event;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public record RetryConfig(
        List<Integer> retryableStatusCodes,
        Integer maxAttemptCount,
        AttemptBackoffConfig attemptBackoffConfig) {

    public static RetryConfig singleAttempt() {
        return new RetryConfig(Collections.emptyList(), 1, AttemptBackoffConfig.noop());
    }

    @FunctionalInterface
    public interface AttemptBackoffConfig {
        Duration getWaitTime(int attemptCount);

        static AttemptBackoffConfig noop() {
            return ignoredAttemptCount -> Duration.ZERO;
        }
    }

    record ConstantBackoffConfig(Duration waitTime) implements AttemptBackoffConfig {
        @Override
        public Duration getWaitTime(int ignoredAttemptCount) {
            return waitTime;
        }
    }
}
