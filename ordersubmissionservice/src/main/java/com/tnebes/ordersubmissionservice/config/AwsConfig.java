package com.tnebes.ordersubmissionservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsConfig {
    private String region;
    private String accountId;
    private String queueName;
    private String queueUrl;
    private String accessKeyId;
    private String secretAccessKey;

    @PostConstruct
    private void updateQueueUrl() {
        Objects.requireNonNull(this.region, "AWS region is missing in configuration.");
        Objects.requireNonNull(this.accountId, "AWS account ID is missing in configuration.");
        Objects.requireNonNull(this.queueName, "AWS queue name is missing in configuration.");
        Objects.requireNonNull(this.accessKeyId, "AWS access key ID is missing in configuration.");
        Objects.requireNonNull(this.secretAccessKey, "AWS secret access key is missing in configuration.");
        this.queueUrl = String.format("https://sqs.%s.amazonaws.com/%s/%s", this.region, this.accountId, this.queueName);
    }
}