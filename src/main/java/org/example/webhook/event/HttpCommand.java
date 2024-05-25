package org.example.webhook.event;

import java.util.Map;

public record HttpCommand(String url, Method method, Map<String, String> headers, Object body) {
    public enum Method {
        GET, POST, PUT, DELETE
    }
}
