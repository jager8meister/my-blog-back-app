package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Payload for creating a new blog post")
public record CreatePostRequestDto(
        @Schema(description = "Post title", example = "Spring Boot tips")
        String title,

        @Schema(description = "Post body", example = "Let's dive into bean lifecycle...")
        String text,

        @Schema(description = "List of post tags", example = "[\"spring\",\"java\"]")
        List<String> tags
) {
}
