package com.tnebes.ordersubmissionservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {

    private long id;
    private String item;
    private int quantity;
    private double totalPrice;
    
}
