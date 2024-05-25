package org.example.webhook.domain.event;

import java.util.Map;

public record HttpCommand(String url, Method method, Map<String, String> headers, String body) implements Command {
    public enum Method {
        GET, POST, PUT, DELETE
    }
}
