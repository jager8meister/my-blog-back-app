package com.myblogbackapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.myblogbackapp.AbstractIntegrationTest;
import com.myblogbackapp.dto.request.CreatePostRequestDto;
import com.myblogbackapp.dto.request.UpdatePostRequestDto;
import com.myblogbackapp.dto.response.PostResponseDto;
import com.myblogbackapp.dto.response.PostsResponseDto;
import com.myblogbackapp.entity.Comment;
import com.myblogbackapp.entity.Post;
import com.myblogbackapp.repository.CommentRepository;
import com.myblogbackapp.repository.PostRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void createPost_persistsEntityAndReturnsDto() {
        CreatePostRequestDto request = new CreatePostRequestDto(
                "Service Post",
                "Body produced by service layer",
                List.of("spring", "boot")
        );

        PostResponseDto response = postService.createPost(request);

        assertThat(response.getId()).isNotNull();
        Post saved = postRepository.findById(response.getId()).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("Service Post");
        assertThat(saved.getTags()).containsExactly("spring", "boot");
        assertThat(saved.getLikesCount()).isZero();
        assertThat(saved.getCommentsCount()).isZero();
    }

    @Test
    void getPosts_appliesSearchAndTagFilters() {
        Post matching = postRepository.save(
                Post.builder()
                        .title("Spring caching handbook")
                        .text("Guide to cache abstraction")
                        .tags(List.of("spring", "cache"))
                        .build()
        );
        postRepository.save(
                Post.builder()
                        .title("Frontend state management")
                        .text("Redux walkthrough")
                        .tags(List.of("react"))
                        .build()
        );

        PostsResponseDto response = postService.getPosts("cache #spring", 1, 5);

        assertThat(response.getPosts())
                .extracting(PostResponseDto::getId)
                .containsExactly(matching.getId());
        assertThat(response.isHasPrev()).isFalse();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getLastPage()).isEqualTo(1);
    }

    @Test
    void updatePost_rewritesTitleTextAndTags() {
        Post post = postRepository.save(
                Post.builder()
                        .title("Legacy title")
                        .text("Legacy body")
                        .tags(List.of("legacy"))
                        .build()
        );

        UpdatePostRequestDto request = new UpdatePostRequestDto(
                "Updated title",
                "Updated body",
                List.of("modern", "spring")
        );

        PostResponseDto updated = postService.updatePost(post.getId(), request);

        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getText()).isEqualTo("Updated body");
        assertThat(updated.getTags()).containsExactly("modern", "spring");
    }

    @Test
    void deletePost_removesPostAndAssociatedComments() {
        Post post = postRepository.save(
                Post.builder()
                        .title("Removable post")
                        .text("To be deleted")
                        .commentsCount(2)
                        .build()
        );
        commentRepository.save(
                Comment.builder()
                        .text("First")
                        .post(post)
                        .build()
        );
        commentRepository.save(
                Comment.builder()
                        .text("Second")
                        .post(post)
                        .build()
        );

        postService.deletePost(post.getId());

        assertThat(postRepository.findById(post.getId())).isEmpty();
        assertThat(commentRepository.findByPostIdOrderByIdAsc(post.getId())).isEmpty();
    }
}
