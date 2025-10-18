package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for updating a comment")
public record UpdateCommentRequestDto(
        @Schema(description = "Updated comment text", example = "I totally agree!")
        String text
) {
}
