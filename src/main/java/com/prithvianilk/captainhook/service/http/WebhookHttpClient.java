package com.prithvianilk.captainhook.service.http;

import com.prithvianilk.captainhook.domain.HttpCommand;
import com.prithvianilk.captainhook.service.retry.RetriableAttemptFailedException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebhookHttpClient {
    HttpClient httpClient;

    public WebhookHttpClient() {
        httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> send(HttpCommand httpCommand) {
        HttpRequest request = getHttpRequest(httpCommand);
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("Failed to sent http request", e);
            throw new RetriableAttemptFailedException();
        }
    }

    public HttpRequest getHttpRequest(HttpCommand httpCommand) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(httpCommand.url()));

        for (Map.Entry<String, String> entry : httpCommand.headers().entrySet()) {
            requestBuilder = requestBuilder.header(entry.getKey(), entry.getValue());
        }

        requestBuilder = switch (httpCommand.method()) {
            case GET -> requestBuilder.GET();
            case POST -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(httpCommand.body()));
            case PUT -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(httpCommand.body()));
            case DELETE -> requestBuilder.DELETE();
        };

        return requestBuilder.build();
    }
}
