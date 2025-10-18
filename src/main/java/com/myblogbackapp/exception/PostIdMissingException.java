package com.myblogbackapp.exception;

public class PostIdMissingException extends RuntimeException {
    public PostIdMissingException(String message) {
        super(message);
    }
}

