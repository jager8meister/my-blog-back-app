package com.myblogbackapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Post image with metadata")
public class PostImageResponseDto {

    @Schema(description = "Binary image data", format = "byte")
    private byte[] data;

    @Schema(description = "MIME type of the image", example = "image/png")
    private String contentType;

    @Schema(description = "Original file name", example = "cover.png")
    private String filename;
}
