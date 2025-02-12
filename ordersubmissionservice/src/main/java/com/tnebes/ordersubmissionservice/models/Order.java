package com.tnebes.ordersubmissionservice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Order {

    private long id;
    private String item;
    private double totalPrice;
    private int quantity;
    private LocalDateTime orderDate;

    public void setOrderDateNow() {
        this.orderDate = LocalDateTime.now();
    }
    
}
