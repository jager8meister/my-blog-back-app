package com.myblogbackapp.exception;

public class PostTextEmptyException extends RuntimeException {
    public PostTextEmptyException(String message) {
        super(message);
    }
}

