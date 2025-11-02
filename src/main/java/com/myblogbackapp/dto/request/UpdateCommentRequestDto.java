package com.myblogbackapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for updating a comment")
public class UpdateCommentRequestDto {

    @Schema(description = "Updated comment text", example = "I totally agree!")
    private String text;
}
