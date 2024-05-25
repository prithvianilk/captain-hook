package com.prithvianilk.captainhook.service;

import lombok.*;
import lombok.experimental.FieldDefaults;
import com.prithvianilk.captainhook.domain.WebhookEvent;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebhookProcessingException extends Exception {
    WebhookEvent webhookEvent;
}
