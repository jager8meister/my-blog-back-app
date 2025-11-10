package com.myblogbackapp.service.impl;

import com.myblogbackapp.dto.request.CreateCommentRequestDto;
import com.myblogbackapp.dto.request.UpdateCommentRequestDto;
import com.myblogbackapp.dto.response.PostCommentResponseDto;
import com.myblogbackapp.entity.Comment;
import com.myblogbackapp.entity.Post;
import com.myblogbackapp.exception.CommentIdentifiersMissingException;
import com.myblogbackapp.exception.CommentNotFoundException;
import com.myblogbackapp.exception.CommentOwnershipException;
import com.myblogbackapp.exception.CommentPostIdMissingException;
import com.myblogbackapp.exception.CommentTextEmptyException;
import com.myblogbackapp.exception.PostNotFoundException;
import com.myblogbackapp.repository.CommentRepository;
import com.myblogbackapp.repository.PostRepository;
import com.myblogbackapp.service.CommentService;
import com.myblogbackapp.mapper.CommentMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<PostCommentResponseDto> getPostComments(Long postId) {
        log.info("Fetching comments for post {}", postId);
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found: " + postId);
        }

        List<Comment> comments = commentRepository.findByPostIdOrderByIdAsc(postId);
        return comments.isEmpty()
                ? List.of()
                : commentMapper.toResponseList(comments);
    }

    @Override
    public PostCommentResponseDto addComment(Long postId, CreateCommentRequestDto request) {
        log.info("Adding comment to post {}", postId);
        if (postId == null) {
            throw new CommentPostIdMissingException("postId must be provided");
        }

        if (request.getText() == null || request.getText().isBlank()) {
            throw new CommentTextEmptyException("Comment text must not be empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));

        Comment comment = commentMapper.toEntity(request, post);
        Comment saved = commentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        log.info("Comment {} added to post {}", saved.getId(), postId);
        return commentMapper.toResponse(saved);
    }

    @Override
    public PostCommentResponseDto updateComment(Long postId, Long commentId, UpdateCommentRequestDto request) {
        log.info("Updating comment {} for post {}", commentId, postId);
        if (commentId == null || postId == null) {
            throw new CommentIdentifiersMissingException("id and postId must be provided");
        }

        if (request.getText() == null || request.getText().isBlank()) {
            throw new CommentTextEmptyException("Comment text must not be empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + commentId));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new CommentOwnershipException("Comment does not belong to the specified post");
        }

        commentMapper.updateEntity(request, comment);
        Comment saved = commentRepository.save(comment);

        log.info("Comment {} updated for post {}", commentId, postId);
        return commentMapper.toResponse(saved);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        log.info("Deleting comment {} for post {}", commentId, postId);
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found: " + postId);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + commentId));

        if (!comment.getPost().getId().equals(postId)) {
            throw new CommentOwnershipException("Comment does not belong to the specified post");
        }

        Post post = comment.getPost();
        commentRepository.delete(comment);

        if (post.getCommentsCount() > 0) {
            post.setCommentsCount(post.getCommentsCount() - 1);
            postRepository.save(post);
        }

        log.info("Comment {} deleted for post {}", commentId, postId);
    }
}
