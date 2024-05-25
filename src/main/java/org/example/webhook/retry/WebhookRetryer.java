package org.example.webhook.retry;

import org.example.webhook.event.Command;
import org.example.webhook.event.RetryConfig;

public interface WebhookRetryer<T extends Command> {
    void attempt(T command, RetryConfig retryConfig);
}
