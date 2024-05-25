package org.example.webhook.domain;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public record HttpCommand(String url, Method method, Map<String, String> headers, String body) implements Command {
    public enum Method {
        GET, POST, PUT, DELETE
    }

    @Override
    public Map<String, String> headers() {
        return Optional.ofNullable(headers).orElse(Collections.emptyMap());
    }
}
