package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "title must not be empty")
    private String title;

    @Schema(description = "Post body", example = "Let's dive into bean lifecycle...")
    @NotBlank(message = "text must not be empty")
    private String text;

    @Schema(description = "List of post tags", example = "[\"spring\",\"java\"]")
    private List<String> tags;
}
