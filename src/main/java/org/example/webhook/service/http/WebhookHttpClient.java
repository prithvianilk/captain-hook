package org.example.webhook.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.webhook.domain.HttpCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }
}
