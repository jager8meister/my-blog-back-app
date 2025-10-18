package com.myblogbackapp.exception;

public class PostImageEmptyException extends RuntimeException {
    public PostImageEmptyException(String message) {
        super(message);
    }
}

