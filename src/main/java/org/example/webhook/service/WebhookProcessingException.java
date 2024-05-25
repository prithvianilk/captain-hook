package org.example.webhook.service;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.webhook.domain.event.WebhookEvent;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebhookProcessingException extends Exception {
    WebhookEvent webhookEvent;
}
