# Webhook example app

- Publish an event w/ a payload, http request details, retry options
- App handles retries

# Design

- Webhook server
    - Consumes from a list of event_types
    - Each event_type is a kafka topic
    - Consumes the event details and attempts a retry
- Webhook client
    - Provides API (just a method bro) to publish an event
    - One implementation would be the kafka based webhook client

### Webhook event

- HttpCommand
    - url
    - method
    - headers
    - body
- Retry config
    - Retryable http success codes
    - Max attempt count