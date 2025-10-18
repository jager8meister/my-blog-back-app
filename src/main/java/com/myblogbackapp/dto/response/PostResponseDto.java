package com.myblogbackapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Post presentation model returned from API")
public record PostResponseDto(
        @Schema(description = "Post identifier", example = "1")
        Long id,

        @Schema(description = "Post title", example = "Spring tips")
        String title,

        @Schema(description = "Post body", example = "Content of the post")
        String text,

        @Schema(description = "Associated tags", example = "[\"spring\",\"java\"]")
        List<String> tags,

        @Schema(description = "Amount of likes", example = "10")
        int likesCount,

        @Schema(description = "Amount of comments", example = "3")
        int commentsCount
) {
}
