package com.tnebes.orderprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

public class OrderProcessor {

    private final SqsClient sqsClient;
    private final SfnClient sfnClient;
    private final String queueUrl;
    private final String stateMachineArn;
    private final Logger logger = LoggerFactory.getLogger(OrderProcessor.class);

    public OrderProcessor(AwsConfig awsConfig) {
        this.sqsClient = SqsClient.builder()
                .region(Region.of(awsConfig.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.sfnClient = SfnClient.builder()
                .region(Region.of(awsConfig.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.queueUrl = this.sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(awsConfig.getQueueName())
                .build()).queueUrl();
        this.stateMachineArn = awsConfig.getStateMachineArn();
        this.logger.info("Order processor initialized with queue URL: {}", this.queueUrl);
    }

    public void startProcessing() {
        while (true) {
            try {
                ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                        .queueUrl(this.queueUrl)
                        .waitTimeSeconds(20)
                        .build();
                List<Message> messages = this.sqsClient.receiveMessage(receiveRequest).messages();
                this.logger.info("Received {} messages", messages.size());

                for (Message message : messages) {
                    this.logger.info("Processing message: {}", message.body());
                    StartExecutionRequest execRequest = StartExecutionRequest.builder()
                            .stateMachineArn(this.stateMachineArn)
                            .input(message.body())
                            .build();
                    this.sfnClient.startExecution(execRequest);
                    this.logger.info("Started execution for message: {}", message.body());

                    this.sqsClient.deleteMessage(DeleteMessageRequest.builder()
                            .queueUrl(this.queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build());
                    this.logger.info("Deleted message: {}", message.body());
                }
            } catch (final RuntimeException e) {
                this.logger.error("Error processing messages", e);
                break;
            }
        }
    }
}
