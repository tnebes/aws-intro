package com.tnebes.orderprocessing;

public class App {
    public static void main(String[] args) {

        try {
            new OrderProcessor().startProcessing();
        } catch (final RuntimeException e) {
            e.printStackTrace();
        }
        
    }
}
