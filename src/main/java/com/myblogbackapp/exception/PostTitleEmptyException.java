package com.myblogbackapp.exception;

public class PostTitleEmptyException extends RuntimeException {
    public PostTitleEmptyException(String message) {
        super(message);
    }
}

