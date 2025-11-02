package com.myblogbackapp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated list of posts")
public class PostsResponseDto {

    @Schema(description = "Collection of posts")
    private List<PostResponseDto> posts;

    @Schema(description = "Indicates presence of previous page", example = "false")
    private boolean hasPrev;

    @Schema(description = "Indicates presence of next page", example = "true")
    private boolean hasNext;

    @Schema(description = "Total amount of pages", example = "5")
    private int lastPage;
}
