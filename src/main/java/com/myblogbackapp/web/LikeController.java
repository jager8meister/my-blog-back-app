package com.myblogbackapp.web;

import com.myblogbackapp.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Likes", description = "Operations with likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/likes")
    @Operation(summary = "Increment likes counter for post")
    public Integer incrementLikes(
            @Parameter(description = "Post identifier", required = true) @PathVariable("postId") Long postId) {
        return likeService.incrementLikes(postId);
    }
}
