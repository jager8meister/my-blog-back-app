package com.myblogbackapp.controller;

import com.myblogbackapp.dto.request.CreateCommentRequestDto;
import com.myblogbackapp.dto.request.UpdateCommentRequestDto;
import com.myblogbackapp.dto.response.PostCommentResponseDto;
import com.myblogbackapp.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Operations with comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/undefined/comments")
    @Operation(summary = "Placeholder endpoint (returns empty list)")
    public List<PostCommentResponseDto> getUndefinedPostComments() {
        return List.of();
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "Get comments of a post")
    public List<PostCommentResponseDto> getPostComments(
            @Parameter(description = "Post identifier", required = true) @PathVariable("postId") Long postId) {
        return commentService.getPostComments(postId);
    }

    @PostMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a comment to a post")
    public PostCommentResponseDto addPostComment(
            @Parameter(description = "Post identifier", required = true) @PathVariable("postId") Long postId,

            @RequestBody CreateCommentRequestDto request
    ) {
        return commentService.addComment(postId, request);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "Update comment text")
    public PostCommentResponseDto updatePostComment(
            @Parameter(description = "Post identifier", required = true) @PathVariable("postId") Long postId,

            @Parameter(description = "Comment identifier", required = true) @PathVariable("commentId") Long commentId,

            @RequestBody UpdateCommentRequestDto request
    ) {
        return commentService.updateComment(postId, commentId, request);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "Delete comment")
    public void deletePostComment(
            @Parameter(description = "Post identifier", required = true) @PathVariable("postId") Long postId,

            @Parameter(description = "Comment identifier", required = true) @PathVariable("commentId") Long commentId
    ) {
        commentService.deleteComment(postId, commentId);
    }
}
