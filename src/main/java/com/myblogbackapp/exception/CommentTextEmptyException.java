package com.myblogbackapp.exception;

public class CommentTextEmptyException extends RuntimeException {
    public CommentTextEmptyException(String message) {
        super(message);
    }
}

