package com.myblogbackapp.service;

import com.myblogbackapp.dto.request.CreateCommentRequestDto;
import com.myblogbackapp.dto.request.UpdateCommentRequestDto;
import com.myblogbackapp.dto.response.PostCommentResponseDto;
import java.util.List;

public interface CommentService {

    List<PostCommentResponseDto> getPostComments(Long postId);

    PostCommentResponseDto addComment(Long postId, CreateCommentRequestDto request);

    PostCommentResponseDto updateComment(Long postId, Long commentId, UpdateCommentRequestDto request);

    void deleteComment(Long postId, Long commentId);
}
