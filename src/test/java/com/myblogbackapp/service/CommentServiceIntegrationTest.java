package com.myblogbackapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.myblogbackapp.AbstractIntegrationTest;
import com.myblogbackapp.dto.request.CreateCommentRequestDto;
import com.myblogbackapp.dto.request.UpdateCommentRequestDto;
import com.myblogbackapp.dto.response.PostCommentResponseDto;
import com.myblogbackapp.entity.Comment;
import com.myblogbackapp.entity.Post;
import com.myblogbackapp.repository.CommentRepository;
import com.myblogbackapp.repository.PostRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        post = postRepository.save(
                Post.builder()
                        .title("Service level post")
                        .text("Body for service tests")
                        .tags(new java.util.ArrayList<>(List.of("service")))
                        .build()
        );
    }

    @Test
    void addComment_incrementsPostCounterAndReturnsDto() {
        PostCommentResponseDto response = commentService.addComment(
                post.getId(),
                new CreateCommentRequestDto("Great write-up!")
        );

        assertThat(response.getId()).isNotNull();
        assertThat(response.getPostId()).isEqualTo(post.getId());
        assertThat(response.getText()).isEqualTo("Great write-up!");

        Post reloaded = postRepository.findById(post.getId()).orElseThrow();
        assertThat(reloaded.getCommentsCount()).isEqualTo(1);
    }

    @Test
    void updateComment_overwritesTextWhenCommentBelongsToPost() {
        Comment comment = commentRepository.save(
                Comment.builder()
                        .text("Original")
                        .post(post)
                        .build()
        );

        PostCommentResponseDto response = commentService.updateComment(
                post.getId(),
                comment.getId(),
                new UpdateCommentRequestDto("Edited")
        );

        assertThat(response.getText()).isEqualTo("Edited");
        Comment reloaded = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(reloaded.getText()).isEqualTo("Edited");
    }

    @Test
    void deleteComment_removesCommentAndDecrementsCounter() {
        Comment comment = commentRepository.save(
                Comment.builder()
                        .text("To delete")
                        .post(post)
                        .build()
        );
        post.setCommentsCount(1);
        postRepository.save(post);

        commentService.deleteComment(post.getId(), comment.getId());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
        Post reloaded = postRepository.findById(post.getId()).orElseThrow();
        assertThat(reloaded.getCommentsCount()).isZero();
    }
}
