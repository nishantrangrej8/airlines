package com.example.airlines.exceptions;


public class EmptyListException extends RuntimeException {

    public EmptyListException(final String msg) {
        super(msg);
    }

    public EmptyListException(final String msg, final Throwable t) {
        super(msg, t);
    }

}