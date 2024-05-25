package org.example;

import org.example.webhook.service.WebhookCreationClient;
import org.example.webhook.domain.event.HttpCommand;
import org.example.webhook.domain.event.RetryConfig;
import org.example.webhook.domain.event.WebhookEvent;
import org.example.webhook.service.WebhookManager;
import org.example.webhook.service.WebhookProcessingServer;
import org.example.webhook.service.kafka.KafkaConsumerWebhookProcessingServer;
import org.example.webhook.service.kafka.KafkaProducerWebhookCreationClient;
import org.example.webhook.service.kafka.KafkaWebhookManager;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<String> eventTypes = List.of("prithvi_event_1", "prithvi_event_2");

        WebhookManager webhookManager = new KafkaWebhookManager();
        webhookManager.registerNewEventTypes(eventTypes);
        System.out.println("Registered events: " + webhookManager.getRegisteredEventTypes());

        WebhookProcessingServer webhookServer = new KafkaConsumerWebhookProcessingServer(eventTypes.getFirst());
        webhookServer.start();

        WebhookCreationClient client = new KafkaProducerWebhookCreationClient();
        while (true) {
            HttpCommand command = new HttpCommand("https://dummyjson.com/users", HttpCommand.Method.GET, Collections.emptyMap(), null);
            client.publish(eventTypes.getFirst(), new WebhookEvent(UUID.randomUUID().toString(), command, RetryConfig.singleAttempt()));
            Thread.sleep(Duration.ofSeconds(10));
        }
    }
}