package com.myblogbackapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representation of a comment")
public class PostCommentResponseDto {

    @Schema(description = "Comment identifier", example = "10")
    private Long id;

    @Schema(description = "Comment content", example = "Awesome read")
    private String text;

    @Schema(description = "Identifier of the related post", example = "1")
    private Long postId;
}
