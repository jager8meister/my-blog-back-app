package com.myblogbackapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({
            PostNotFoundException.class,
            CommentNotFoundException.class,
            PostImageNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({
            PostImageEmptyException.class,
            PostImageProcessingException.class,
            PostIdMissingException.class,
            PostTitleEmptyException.class,
            PostTextEmptyException.class,
            CommentPostIdMissingException.class,
            CommentTextEmptyException.class,
            CommentIdentifiersMissingException.class,
            CommentOwnershipException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(ex.getMessage()));
    }
}

