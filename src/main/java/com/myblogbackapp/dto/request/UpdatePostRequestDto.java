package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Payload for updating an existing blog post")
public record UpdatePostRequestDto(
        @Schema(description = "Updated post title", example = "Updated Spring Boot tips")
        String title,

        @Schema(description = "Updated post body", example = "New post content")
        String text,

        @Schema(description = "Updated list of tags", example = "[\"spring\",\"boot\"]")
        List<String> tags
) {
}
