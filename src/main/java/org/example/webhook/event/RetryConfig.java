package org.example.webhook.event;

import java.util.Collections;
import java.util.List;

public record RetryConfig(List<Integer> retryableStatusCodes, Integer maxAttemptCount) {
    public static RetryConfig singleAttempt() {
        return new RetryConfig(Collections.emptyList(), 1);
    }
}
