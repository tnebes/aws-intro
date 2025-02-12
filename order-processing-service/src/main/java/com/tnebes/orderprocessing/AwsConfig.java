package com.tnebes.orderprocessing;

import lombok.Getter;

@Getter
public class AwsConfig {

    private final String region;
    private final String accountId;
    private final String queueName;
    private final String queueUrl;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String stateMachineName;
    private final String stateMachineArn;

    public AwsConfig() {
        this.region = this.getRequiredEnv("AWS_REGION");
        this.accountId = this.getRequiredEnv("AWS_ACCOUNT_ID");
        this.queueName = this.getRequiredEnv("AWS_QUEUE_NAME");
        this.queueUrl = String.format("https://sqs.%s.amazonaws.com/%s/%s", this.region, this.accountId, this.queueName);
        this.accessKeyId = this.getRequiredEnv("AWS_ACCESS_KEY_ID");
        this.secretAccessKey = this.getRequiredEnv("AWS_SECRET_ACCESS_KEY");
        this.stateMachineName = this.getRequiredEnv("AWS_STATE_MACHINE_NAME");
        this.stateMachineArn = String.format("arn:aws:states:%s:%s:stateMachine:%s",
                this.region, this.accountId, this.stateMachineName);
    }

    private String getRequiredEnv(String variableName) {
        String value = System.getenv(variableName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: " + variableName);
        }
        return value;
    }

}
