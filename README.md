# Captain hook

- A simple webhook as a service

# Features

- Publish a webhook event w/ http request details
- Automatically handles retries on failures (timeouts / based on status code)
- Manually retry webhooks that failed after the defined limit
- Monitor all webhooks and their status

# Design

### Event type

- Unique id representing the webhook event type
- Retry configuration
    - Request timeout
    - Max attempt count
    - HTTP status codes considered as a successful processing of the webhook

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

- Validate if event_type already exists
- If not, create new event type w/ retry configuration
- Create topic with event name on kafka

### Create webhook

- Find if webhook already exists with webhook_id and event_type
- Create webhook w/ webhook_id, event_type, http_command, status = CREATED
- Publish webhook event to kafka

### Process webhook

- Consume events from kafka
- Read retry config from db
- Attempt processing of event with retry config
    - Sleep application thread on wait
- If processing successful, update status of event to COMPLETED
- Else, update status to FAILED
- Commit message on kafka

### View all webhooks by event_type and status

- Find all webhooks by status