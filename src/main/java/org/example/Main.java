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

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<String> eventTypes = List.of("prithvi_event_1", "prithvi_event_2");

        WebhookManager webhookManager = new KafkaWebhookManager();
        webhookManager.registerNewEventTypes(eventTypes);
        System.out.println("Events: " + webhookManager.getEventTypes());

        WebhookServer webhookServer = new KafkaConsumerWebhookServer(eventTypes);
        webhookServer.start();

        WebhookClient client = new KafkaProducerWebhookClient();
        for (int i = 0; i < 10; ++i) {
            HttpCommand command = new HttpCommand("https://dummyjson.com/users", HttpCommand.Method.GET, Collections.emptyMap(), null);
            client.publish(eventTypes.get(0), new WebhookEvent(command, RetryConfig.singleAttempt()));
        }

        Thread.sleep(Duration.ofMinutes(5));
    }
}