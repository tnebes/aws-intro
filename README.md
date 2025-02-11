# Decoupled Order Processing System Training Exercise

This exercise demonstrates a simple, decoupled architecture using AWS services with Java. The solution comprises two microservices that communicate via Amazon SQS. One service submits order messages, while the other processes them through a state machine and persists data in DynamoDB.

---

## Goals

1. **Two AWS Services in Java**  
   Develop two microservices:
   - A submission service to send messages.
   - A processing service to receive and handle messages.

2. **Communication via Amazon SQS**  
   Use Amazon SQS to decouple the services, ensuring asynchronous and resilient message delivery.

3. **Decoupled Architecture**  
   The services do not depend on each other’s availability; they interact solely via the SQS queue.

4. **DynamoDB Usage**  
   Persist order (or message) data in a DynamoDB table to simulate a persistence layer.

5. **State Machine Integration**  
   Incorporate AWS Step Functions to orchestrate a multi-step workflow in the processing service (for example, to validate orders, check inventory, or simulate payment processing) before updating DynamoDB.

---

## Architecture Overview

- **Order Submission Service**  
  Exposes a REST API (or similar interface) that receives new order requests and sends a message to an SQS queue using the AWS SDK for Java.

- **Order Processing Service**  
  Polls the SQS queue and, upon receiving a message, triggers an AWS Step Functions state machine. This state machine manages the sequential (or parallel) processing steps and, upon successful completion, writes order details to DynamoDB.

- **AWS Components Involved**  
  - *Amazon SQS:* Facilitates decoupled, asynchronous message passing.
  - *AWS Step Functions:* Implements a state machine to orchestrate processing steps.
  - *Amazon DynamoDB:* Serves as the persistence layer for order data.

---

## Implementation Steps

1. **Setup AWS Resources**  
   - Create an Amazon SQS queue.
   - Create a DynamoDB table to store order records.
   - Define a state machine (using AWS Step Functions) with several simple steps (e.g. order validation, processing simulation, and persistence).

2. **Develop the Order Submission Service**  
   - Use the AWS SDK for Java to send order messages to the SQS queue.
   - Ensure messages contain all required order details in JSON format.

3. **Develop the Order Processing Service**  
   - Poll the SQS queue for new messages.
   - Upon receipt, invoke the defined state machine.
   - The state machine processes the order (handling retries or errors as needed) and finally writes the order record to DynamoDB.

4. **Testing the End-to-End Flow**  
   - Submit an order via the submission service.
   - Verify that the message appears in the queue.
   - Confirm that the state machine processes the message and that the order data is stored in DynamoDB.

---

## Additional Resources

- **Official Documentation**  
  - [AWS Step Functions Developer Guide](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html)
  - [AWS SDK for Java 2.x Developer Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
  - [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)

