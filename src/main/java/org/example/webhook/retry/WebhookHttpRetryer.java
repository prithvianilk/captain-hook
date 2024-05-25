package org.example.webhook.retry;

import org.example.webhook.event.HttpCommand;
import org.example.webhook.event.RetryConfig;
import org.example.webhook.http.WebhookHttpClient;

import java.net.http.HttpResponse;
import java.time.Duration;

public class WebhookHttpRetryer implements WebhookRetryer<HttpCommand> {
    private final WebhookHttpClient webhookHttpClient;

    public WebhookHttpRetryer(WebhookHttpClient webhookHttpClient) {
        this.webhookHttpClient = webhookHttpClient;
    }

    @Override
    public void attempt(HttpCommand httpCommand, RetryConfig retryConfig) {
        for (int attemptCount = 1; attemptCount <= retryConfig.maxAttemptCount(); ++attemptCount) {
            HttpResponse<String> response = webhookHttpClient.send(httpCommand);

            if (!shouldRetry(retryConfig, response, attemptCount)) {
                break;
            }

            waitBeforeNextAttempt(retryConfig, attemptCount);
        }
    }

    private boolean shouldRetry(RetryConfig retryConfig, HttpResponse<String> response, int attemptCount) {
        return attemptCount < retryConfig.maxAttemptCount()
                && retryConfig.retryableStatusCodes().contains(response.statusCode());
    }

    private void waitBeforeNextAttempt(RetryConfig retryConfig, int retryAttempt) {
        try {
            Duration waitDuration = retryConfig.attemptBackoffConfig().getWaitTime(retryAttempt);
            Thread.sleep(waitDuration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
