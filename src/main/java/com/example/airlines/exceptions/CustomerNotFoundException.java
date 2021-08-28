package com.example.airlines.exceptions;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(final String msg) {
        super(msg);
    }

    public CustomerNotFoundException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
