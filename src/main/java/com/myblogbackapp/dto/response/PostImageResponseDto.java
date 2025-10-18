package com.myblogbackapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Post image with metadata")
public record PostImageResponseDto(
        @Schema(description = "Binary image data", format = "byte")
        byte[] data,

        @Schema(description = "MIME type of the image", example = "image/png")
        String contentType,

        @Schema(description = "Original file name", example = "cover.png")
        String filename
) {
}
