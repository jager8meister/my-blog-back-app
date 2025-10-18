package com.myblogbackapp.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error message returned by the API")
public record ApiErrorResponse(
        @Schema(description = "Human readable error message", example = "Post not found: 999")
        String message
) {
}
