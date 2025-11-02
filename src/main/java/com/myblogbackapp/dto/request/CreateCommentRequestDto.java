package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a comment")
public class CreateCommentRequestDto {

    @Schema(description = "Comment text", example = "Great article!")
    private String text;
}
