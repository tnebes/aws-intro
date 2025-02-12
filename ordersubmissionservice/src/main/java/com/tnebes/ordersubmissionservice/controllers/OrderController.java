package com.tnebes.ordersubmissionservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tnebes.ordersubmissionservice.config.AwsConfig;
import com.tnebes.ordersubmissionservice.models.Order;
import com.tnebes.ordersubmissionservice.models.OrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final SqsClient sqsClient;
    private final String queueUrl;
    private final ObjectMapper objectMapper;

    public OrderController(AwsConfig awsConfig) {
        this.queueUrl = awsConfig.getQueueUrl();
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
            awsConfig.getAccessKeyId(), 
            awsConfig.getSecretAccessKey()
        );
        this.sqsClient = SqsClient.builder()
                .region(Region.of(awsConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @PostMapping
    public ResponseEntity<String> submitOrder(@RequestBody OrderDto order) throws JsonProcessingException {
        try {
            Order mappedOrder = new Order();
            mappedOrder.setId(order.getId());
            mappedOrder.setItem(order.getItem());
            mappedOrder.setQuantity(order.getQuantity());
            mappedOrder.setTotalPrice(order.getTotalPrice());
            mappedOrder.setOrderDateNow();
            String orderJson = objectMapper.writeValueAsString(mappedOrder);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(orderJson)
                    .build();
            this.sqsClient.sendMessage(sendMsgRequest);
            return ResponseEntity.ok("Order submitted successfully.");
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid order format.");
        }
    }
}
