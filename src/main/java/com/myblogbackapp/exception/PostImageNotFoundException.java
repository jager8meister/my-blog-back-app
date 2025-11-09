package com.myblogbackapp.exception;

public class PostImageNotFoundException extends RuntimeException {
    public PostImageNotFoundException(String message) {
        super(message);
    }
}

