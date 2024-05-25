package org.example.webhook.retry;

import org.example.webhook.event.HttpCommand;
import org.example.webhook.event.RetryConfig;
import org.example.webhook.http.WebhookHttpClient;

import java.net.http.HttpResponse;

public class WebhookHttpRetryer extends WebhookRetryer<HttpCommand, HttpResponse<String>> {
    private final WebhookHttpClient webhookHttpClient;

    public WebhookHttpRetryer(WebhookHttpClient webhookHttpClient) {
        this.webhookHttpClient = webhookHttpClient;
    }

    @Override
    protected HttpResponse<String> attempt(HttpCommand httpCommand, RetryConfig retryConfig) {
        HttpResponse<String> response = webhookHttpClient.send(httpCommand);
        logResponse(response);
        return response;
    }

    private void logResponse(HttpResponse<String> response) {
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }

    @Override
    protected boolean shouldRetry(RetryConfig retryConfig, HttpResponse<String> response, int attemptCount) {
        return attemptCount < retryConfig.maxAttemptCount()
                && retryConfig.retryableStatusCodes().contains(response.statusCode());
    }
}
