package com.myblogbackapp.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myblogbackapp.entity.Comment;
import com.myblogbackapp.entity.Post;
import com.myblogbackapp.repository.CommentRepository;
import com.myblogbackapp.repository.PostRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Post post;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        post = postRepository.save(
                Post.builder()
                        .title("Post title")
                        .text("Post body")
                        .tags(List.of())
                        .likesCount(0)
                        .commentsCount(0)
                        .build());
    }

    @Test
    void addComment_incrementsCounterAndReturnsResponse() throws Exception {
        String payload = objectMapper.writeValueAsString(new CommentPayload("Great article!"));

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.text").value("Great article!"));

        Post reloaded = postRepository.findById(post.getId()).orElseThrow();
        assertThat(reloaded.getCommentsCount()).isEqualTo(1);
    }

    @Test
    void getComments_returnsOrderedList() throws Exception {
        commentRepository.save(
                Comment.builder()
                        .text("First")
                        .post(post)
                        .build());
        commentRepository.save(
                Comment.builder()
                        .text("Second")
                        .post(post)
                        .build());
        post.setCommentsCount(2);
        postRepository.save(post);

        mockMvc.perform(get("/api/posts/{postId}/comments", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].text").value("First"))
                .andExpect(jsonPath("$[1].text").value("Second"));
    }

    @Test
    void getComments_missingPost_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}/comments", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found: 9999"));
    }

    @Test
    void updateComment_updatesText() throws Exception {
        long commentId = createComment("Original");
        String payload = objectMapper.writeValueAsString(new CommentPayload("Updated text"));

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", post.getId(), commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.text").value("Updated text"));

        Comment reloaded = commentRepository.findById(commentId).orElseThrow();
        assertThat(reloaded.getText()).isEqualTo("Updated text");
    }

    @Test
    void deleteComment_decrementsCounter() throws Exception {
        long commentId = createComment("Will be removed");

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", post.getId(), commentId))
                .andExpect(status().isOk());

        assertThat(commentRepository.existsById(commentId)).isFalse();
        Post reloaded = postRepository.findById(post.getId()).orElseThrow();
        assertThat(reloaded.getCommentsCount()).isZero();
    }

    @Test
    void addComment_withBlankText_returnsBadRequest() throws Exception {
        String payload = objectMapper.writeValueAsString(new CommentPayload("   "));

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Comment text must not be empty"));
    }

    @Test
    void addComment_forMissingPost_returnsNotFound() throws Exception {
        String payload = objectMapper.writeValueAsString(new CommentPayload("hello"));

        mockMvc.perform(post("/api/posts/{postId}/comments", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found: 9999"));
    }

    @Test
    void updateComment_withBlankText_returnsBadRequest() throws Exception {
        long commentId = createComment("Original");
        String payload = objectMapper.writeValueAsString(new CommentPayload("   "));

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", post.getId(), commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Comment text must not be empty"));
    }

    @Test
    void updateComment_commentFromAnotherPost_returnsBadRequest() throws Exception {
        Post anotherPost = postRepository.save(
                Post.builder()
                        .title("Other")
                        .text("Body")
                        .tags(List.of())
                        .likesCount(0)
                        .commentsCount(0)
                        .build());
        Comment foreignComment = commentRepository.save(
                Comment.builder()
                        .text("Foreign")
                        .post(anotherPost)
                        .build());
        anotherPost.setCommentsCount(1);
        postRepository.save(anotherPost);

        String payload = objectMapper.writeValueAsString(new CommentPayload("Updated"));

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", post.getId(), foreignComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Comment does not belong to the specified post"));
    }

    @Test
    void deleteComment_commentFromAnotherPost_returnsBadRequest() throws Exception {
        Post anotherPost = postRepository.save(
                Post.builder()
                        .title("Other")
                        .text("Body")
                        .tags(List.of())
                        .likesCount(0)
                        .commentsCount(0)
                        .build());
        Comment foreignComment = commentRepository.save(
                Comment.builder()
                        .text("Foreign")
                        .post(anotherPost)
                        .build());

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", post.getId(), foreignComment.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Comment does not belong to the specified post"));
    }

    private long createComment(String text) throws Exception {
        String payload = objectMapper.writeValueAsString(new CommentPayload(text));
        MvcResult result = mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    private record CommentPayload(String text) {}
}
