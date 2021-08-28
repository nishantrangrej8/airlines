package com.example.airlines.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@Setter
@ToString
public class Customer {

    private String customerName;
    private int miles;

    public Customer(String customerName, int miles) {
        this.customerName = customerName;
        this.miles = miles;
    }

}
