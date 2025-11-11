package com.myblogbackapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.myblogbackapp.AbstractIntegrationTest;
import com.myblogbackapp.entity.Post;
import com.myblogbackapp.exception.PostNotFoundException;
import com.myblogbackapp.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LikeServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    void incrementLikes_returnsUpdatedCounter() {
        Post post = postRepository.save(
                Post.builder()
                        .title("Likeable post")
                        .text("Counting likes")
                        .likesCount(5)
                        .build()
        );

        int newCount = likeService.incrementLikes(post.getId());

        assertThat(newCount).isEqualTo(6);
        assertThat(postRepository.findById(post.getId()))
                .get()
                .extracting(Post::getLikesCount)
                .isEqualTo(6);
    }

    @Test
    void incrementLikes_missingPost_throwsException() {
        assertThatThrownBy(() -> likeService.incrementLikes(999L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("999");
    }
}
