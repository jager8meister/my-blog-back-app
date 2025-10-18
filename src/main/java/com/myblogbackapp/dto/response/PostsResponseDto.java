package com.myblogbackapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list of posts")
public record PostsResponseDto(
        @Schema(description = "Collection of posts")
        List<PostResponseDto> posts,

        @Schema(description = "Indicates presence of previous page", example = "false")
        boolean hasPrev,

        @Schema(description = "Indicates presence of next page", example = "true")
        boolean hasNext,

        @Schema(description = "Total amount of pages", example = "5")
        int lastPage
) {
}
