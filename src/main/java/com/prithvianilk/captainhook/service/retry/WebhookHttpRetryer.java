package com.prithvianilk.captainhook.service.retry;

import com.prithvianilk.captainhook.domain.HttpCommand;
import com.prithvianilk.captainhook.domain.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import com.prithvianilk.captainhook.service.http.WebhookHttpClient;

import java.net.http.HttpResponse;

@Slf4j
public class WebhookHttpRetryer extends WebhookRetryer<HttpCommand, HttpResponse<String>> {
    private final WebhookHttpClient webhookHttpClient;

    public WebhookHttpRetryer(WebhookHttpClient webhookHttpClient) {
        this.webhookHttpClient = webhookHttpClient;
    }

    @Override
    protected HttpResponse<String> attempt(HttpCommand httpCommand, RetryConfig retryConfig) {
        HttpResponse<String> response = webhookHttpClient.send(httpCommand);
        log.info("Status code: {}", response.statusCode());
        log.info("Headers: {}", response.headers());
        log.info("Body: {}", response.body());
        return response;
    }

    @Override
    protected boolean shouldRetry(RetryConfig retryConfig, HttpResponse<String> response, int attemptCount) {
        return !retryConfig.successStatusCode().contains(response.statusCode());
    }
}
