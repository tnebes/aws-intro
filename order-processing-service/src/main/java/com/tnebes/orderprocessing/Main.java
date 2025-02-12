package com.tnebes.orderprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Main.class);

        try {
            new OrderProcessor(new AwsConfig()).startProcessing();
            logger.info("Order processor started");
        } catch (final RuntimeException e) {
            logger.error("Error starting order processor", e);
        }
    }
}
