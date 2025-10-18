package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for creating a comment")
public record CreateCommentRequestDto(
        @Schema(description = "Comment text", example = "Great article!")
        String text
) {
}
