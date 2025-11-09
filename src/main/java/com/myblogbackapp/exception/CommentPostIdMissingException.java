package com.myblogbackapp.exception;

public class CommentPostIdMissingException extends RuntimeException {
    public CommentPostIdMissingException(String message) {
        super(message);
    }
}

