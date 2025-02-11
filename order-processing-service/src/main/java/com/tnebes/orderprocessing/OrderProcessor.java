package com.tnebes.orderprocessing;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import java.util.List;

public class OrderProcessor {

    private static final String STATE_MACHINE_ARN = "arn:aws:states:us-east-1:123456789012:stateMachine:YourStateMachine";
    private final SqsClient sqsClient;
    private final SfnClient sfnClient;
    private final String queueUrl;

    public OrderProcessor() {
        this.sqsClient = SqsClient.builder()
                .region(Region.of("us-east-1"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.sfnClient = SfnClient.builder()
                .region(Region.of("us-east-1"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.queueUrl = this.sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName("YourQueueName")
                .build()).queueUrl();
    }

    public void startProcessing() {
        while (true) {
            try {
                ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                        .queueUrl(this.queueUrl)
                        .waitTimeSeconds(20)
                        .build();
                List<Message> messages = this.sqsClient.receiveMessage(receiveRequest).messages();

                for (Message message : messages) {
                    StartExecutionRequest execRequest = StartExecutionRequest.builder()
                            .stateMachineArn(STATE_MACHINE_ARN)
                            .input(message.body())
                            .build();
                    this.sfnClient.startExecution(execRequest);

                    this.sqsClient.deleteMessage(DeleteMessageRequest.builder()
                            .queueUrl(this.queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build());
                }
            } catch (final RuntimeException e) {
                System.err.println(e.getMessage());
                break;
            }
        }
    }
}
