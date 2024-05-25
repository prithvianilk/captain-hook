package org.example.webhook.event;

import java.util.Map;

public record HttpCommand(String url, Method method, Map<String, String> headers, Object body) implements Command {
    public enum Method {
        GET, POST, PUT, DELETE
    }
}
