package org.example.webhook.service.retry;

import org.example.webhook.domain.event.Command;
import org.example.webhook.domain.event.RetryConfig;

import java.time.Duration;

public abstract class WebhookRetryer<T extends Command, Y> {
    protected abstract Y attempt(T command, RetryConfig retryConfig);

    protected abstract boolean shouldRetry(RetryConfig retryConfig, Y attemptResult, int attemptCount);

    public void attemptWithRetry(T command, RetryConfig retryConfig) {
        for (int attemptCount = 1; attemptCount <= retryConfig.maxAttemptCount(); ++attemptCount) {
            Y attemptResult = attempt(command, retryConfig);

            if (!shouldRetry(retryConfig, attemptResult, attemptCount)) {
                break;
            }

            waitBeforeNextAttempt(retryConfig, attemptCount);
        }
    }

    private void waitBeforeNextAttempt(RetryConfig retryConfig, int attemptCount) {
        try {
            Duration waitDuration = retryConfig.attemptBackoffConfig().getWaitTime(attemptCount);
            Thread.sleep(waitDuration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
