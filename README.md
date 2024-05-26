# Captain hook

- A simple webhook as a service
- Built it over a late sunday night / early sunday morning, wanted to learn a bit of kafka

# Features

- Publish a webhook event w/ http request details via a RESTful web JSON
- Automatically handles retries on failures (timeouts / based on status code)
- Manually retry webhooks that failed after the defined limit
- Monitor all webhooks and their status

## APIs

### Create event type

```
curl --location '{{base_url}}/event/v1' \
--header 'Content-Type: application/json' \
--data '{
    "id": "prithvi_event_1"
}'
```

### Get all event types

```
curl --location '{{base_url}}/event/v1'
```

### Create webhook

```
curl --location '{{base_url}}/webhook/v1' \
--header 'Content-Type: application/json' \
--data '{
    "id": "2f616df0-4768-4bf7-803d-4000aa4b52d8",
    "event_type": "prithvi_event_1",
    "command": {
        "type": "HTTP",
        "method": "GET",
        "url": "https://dummyjson.com/products"
    }
}'
```

### Get all webhooks by event type

```
curl --location '{{base_url}}/webhook/v1/prithvi_event_1'
```

# Design

### Event type

- Unique id representing the webhook event type
- Retry configuration
    - Max attempt count
    - HTTP status codes considered as a successful processing of the webhook
    - Attempt backoff config - constant, exponential, etc

### Webhook event

- Unique webhook id: String
- Event type: String
- HttpCommand
    - url: String
    - method: GET / POST / PUT / DELETE
    - headers: Map<String, String>
    - body: String

## Flows

### Create event type

- Create new event type w/ retry configuration
- Create topic with event name on kafka
- Publish event on internal topic to notify webhook processors

### Create webhook

- Create new webhook w/ webhook_id, event_type, http_command, status = CREATED
- Publish webhook event to kafka

### Webhook processor worker starts

- Read all event_types
- Start a webhook processor for each event_type

### Start new webhook processor for new event

- Start kafka consumer subscribing to internal topic for new event_types
- Consume new event type and create new event_type processor if not exists

### Process webhook

- Consume webhook events from kafka
- Attempt processing of event with retry config
    - Sleep application thread on wait
- Commit message on kafka
- If processing successful, update status of event to COMPLETED
- Else, update status to FAILED

### View all webhooks by event_type and status

- Find all webhooks by event_type &/ status