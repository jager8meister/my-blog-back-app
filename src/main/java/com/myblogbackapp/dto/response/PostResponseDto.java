package com.myblogbackapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Post presentation model returned from API")
public class PostResponseDto {

    @Schema(description = "Post identifier", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Spring tips")
    private String title;

    @Schema(description = "Post body", example = "Content of the post")
    private String text;

    @Schema(description = "Associated tags", example = "[\"spring\",\"java\"]")
    private List<String> tags;

    @Schema(description = "Amount of likes", example = "10")
    private int likesCount;

    @Schema(description = "Amount of comments", example = "3")
    private int commentsCount;
}
