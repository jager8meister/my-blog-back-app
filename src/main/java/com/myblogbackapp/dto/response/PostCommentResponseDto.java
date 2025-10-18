package com.myblogbackapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representation of a comment")
public record PostCommentResponseDto(
        @Schema(description = "Comment identifier", example = "10")
        Long id,

        @Schema(description = "Comment content", example = "Awesome read")
        String text,

        @Schema(description = "Identifier of the related post", example = "1")
        Long postId
) {
}
