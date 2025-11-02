package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for updating an existing blog post")
public class UpdatePostRequestDto {

    @Schema(description = "Updated post title", example = "Updated Spring Boot tips")
    private String title;

    @Schema(description = "Updated post body", example = "New post content")
    private String text;

    @Schema(description = "Updated list of tags", example = "[\"spring\",\"boot\"]")
    private List<String> tags;
}
