package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a new blog post")
public class CreatePostRequestDto {

    @Schema(description = "Post title", example = "Spring Boot tips")
    private String title;

    @Schema(description = "Post body", example = "Let's dive into bean lifecycle...")
    private String text;

    @Schema(description = "List of post tags", example = "[\"spring\",\"java\"]")
    private List<String> tags;
}
