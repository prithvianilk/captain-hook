package com.prithvianilk.captainhook.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prithvianilk.captainhook.domain.HttpCommand;
import com.prithvianilk.captainhook.service.retry.CommandAttemptFailedException;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Slf4j
public class WebhookHttpClient {
    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public WebhookHttpClient() {
        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    public HttpResponse<String> send(HttpCommand httpCommand) {
        HttpRequest request = getHttpRequest(httpCommand);
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("Failed to sent http request", e);
            throw new CommandAttemptFailedException();
        }
    }

    public HttpRequest getHttpRequest(HttpCommand httpCommand) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(httpCommand.url()));

        for (Map.Entry<String, String> entry : httpCommand.headers().entrySet()) {
            requestBuilder = requestBuilder.header(entry.getKey(), entry.getValue());
        }

        requestBuilder = switch (httpCommand.method()) {
            case GET -> requestBuilder.GET();
            case POST -> requestBuilder.POST(getStringBodyPublisher(httpCommand));
            case PUT -> requestBuilder.PUT(getStringBodyPublisher(httpCommand));
            case DELETE -> requestBuilder.DELETE();
        };

        return requestBuilder.build();
    }

    private HttpRequest.BodyPublisher getStringBodyPublisher(HttpCommand httpCommand) {
        try {
            String bodyAsString = objectMapper.writeValueAsString(httpCommand.body());
            return HttpRequest.BodyPublishers.ofString(bodyAsString);
        } catch (JsonProcessingException e) {
            log.error("Failed to write body as string", e);
            throw new CommandAttemptFailedException();
        }
    }
}
