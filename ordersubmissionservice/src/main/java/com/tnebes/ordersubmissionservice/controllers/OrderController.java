package com.tnebes.ordersubmissionservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnebes.ordersubmissionservice.models.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@RestController
@RequestMapping("/orders")
public class OrderController {

    // Ideally load this from configuration
    private static final String queueUrl = "https://sqs.<region>.amazonaws.com/<account-id>/YourQueueName";
    private final SqsClient sqsClient;

    public OrderController() {
        this.sqsClient = SqsClient.builder()
                .region(Region.of("your-region"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @PostMapping
    public ResponseEntity<String> submitOrder(@RequestBody Order order) throws JsonProcessingException {
        try {
            String orderJson = new ObjectMapper().writeValueAsString(order);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(orderJson)
                    .build();
            this.sqsClient.sendMessage(sendMsgRequest);
            return ResponseEntity.ok("Order submitted successfully.");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid order format.");
        }
    }
}
