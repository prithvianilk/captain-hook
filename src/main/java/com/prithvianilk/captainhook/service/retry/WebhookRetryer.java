package com.prithvianilk.captainhook.service.retry;

import com.prithvianilk.captainhook.domain.Command;
import com.prithvianilk.captainhook.domain.RetryConfig;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public abstract class WebhookRetryer<C extends Command, R> {
    protected abstract R attempt(C command, RetryConfig retryConfig);

    protected abstract boolean shouldRetry(RetryConfig retryConfig, R attemptResult, int attemptCount);

    public void attemptWithRetry(C command, RetryConfig retryConfig) {
        for (int attemptCount = 1; attemptCount <= retryConfig.maxAttemptCount(); ++attemptCount) {
            try {
                R attemptResult = attempt(command, retryConfig);
                if (!shouldRetry(retryConfig, attemptResult, attemptCount)) {
                    return;
                }
            } catch (RetriableAttemptFailedException e) {
                log.debug("Received retryable exception on attempt", e);
            }

            if (attemptCount < retryConfig.maxAttemptCount()) {
                waitBeforeNextAttempt(retryConfig, attemptCount);
            }
        }

        throw new CommandAttemptFailedException();
    }

    private void waitBeforeNextAttempt(RetryConfig retryConfig, int attemptCount) {
        try {
            Duration waitDuration = retryConfig.attemptBackoffConfig().getWaitTime(attemptCount);
            Thread.sleep(waitDuration);
        } catch (InterruptedException e) {
            log.error("Failed on waiting before next attempt", e);
            throw new CommandAttemptFailedException();
        }
    }
}
