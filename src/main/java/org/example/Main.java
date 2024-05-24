package org.example;

import java.time.Duration;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        WebhookServer webhookServer = new KafkaWebhookServer("prithvi_event_1", "prithvi_event_2");
        webhookServer.start();
        Thread.sleep(Duration.ofMinutes(5));
    }
}