package com.myblogbackapp.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myblogbackapp.entity.Post;
import com.myblogbackapp.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LikeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    void incrementLikes_returnsUpdatedCount() throws Exception {
//        Post post = postRepository.save(new Post(null, "Title", "Body", null, 0, 0));
        Post post = postRepository.save(Post.builder()
                .title("Title")
                .text("Body")
                .build());

        mockMvc.perform(post("/api/posts/{postId}/likes", post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        Post reloaded = postRepository.findById(post.getId()).orElseThrow();
        assertThat(reloaded.getLikesCount()).isEqualTo(1);
    }

    @Test
    void incrementLikes_missingPost_returnsNotFound() throws Exception {
        mockMvc.perform(post("/api/posts/{postId}/likes", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found: 9999"));
    }
}
