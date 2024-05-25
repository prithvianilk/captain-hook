package org.example;

import org.example.webhook.WebhookClient;
import org.example.webhook.event.HttpCommand;
import org.example.webhook.event.RetryConfig;
import org.example.webhook.event.WebhookEvent;
import org.example.webhook.WebhookManager;
import org.example.webhook.WebhookServer;
import org.example.webhook.kafka.KafkaConsumerWebhookServer;
import org.example.webhook.kafka.KafkaProducerWebhookClient;
import org.example.webhook.kafka.KafkaWebhookManager;

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

        WebhookServer webhookServer = new KafkaConsumerWebhookServer(eventTypes.getFirst());
        webhookServer.start();

        WebhookClient client = new KafkaProducerWebhookClient();
        while (true) {
            HttpCommand command = new HttpCommand("https://dummyjson.com/users", HttpCommand.Method.GET, Collections.emptyMap(), null);
            client.publish(eventTypes.getFirst(), new WebhookEvent(UUID.randomUUID().toString(), command, RetryConfig.singleAttempt()));
            Thread.sleep(Duration.ofSeconds(10));
        }
    }
}