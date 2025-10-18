package com.myblogbackapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myblogbackapp.entity.Comment;
import com.myblogbackapp.entity.Post;
import com.myblogbackapp.repository.CommentRepository;
import com.myblogbackapp.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void listPosts_filtersByTextAndReturnsMatch() throws Exception {
//        Post springPost = postRepository.save(new Post(null, "Spring Tips", "Deep dive into Spring Boot search", List.of("spring"), 0, 0));
        Post springPost = postRepository.save(
                Post.builder()
                        .title("Spring Tips")
                        .text("Deep dive into Spring Boot search")
                        .tags(List.of("spring")).build()
        );
//        postRepository.save(new Post(null, "Kotlin Tricks", "Functional programming", List.of("kotlin"), 0, 0));
        postRepository.save(Post.builder()
                .title("Kotlin Tricks")
                .text("Functional programming")
                .tags(List.of("kotlin"))
                .build());

        mockMvc.perform(get("/api/posts")
                        .param("search", "search")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(1))
                .andExpect(jsonPath("$.posts[0].id").value(springPost.getId()))
                .andExpect(jsonPath("$.posts[0].title").value("Spring Tips"));
    }

    @Test
    void createPost_persistsEntity() throws Exception {
        String payload = objectMapper.writeValueAsString(new PostCreationPayload("New Post", "Fresh content", List.of("news")));

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("New Post"))
                .andExpect(jsonPath("$.text").value("Fresh content"))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));

        assertThat(postRepository.findAll()).hasSize(1);
    }

    @Test
    void getPost_returnsPostResponse() throws Exception {
        Post post = postRepository.save(
                Post.builder()
                        .title("Original")
                        .text("Body")
                        .tags(List.of("tag"))
                        .likesCount(3)
                        .commentsCount(2)
                        .build());

        mockMvc.perform(get("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("Original"))
                .andExpect(jsonPath("$.likesCount").value(3))
                .andExpect(jsonPath("$.commentsCount").value(2));
    }

    @Test
    void updatePost_modifiesPersistedEntity() throws Exception {
        Post post = postRepository.save(
                Post.builder()
                        .title("Original")
                        .text("Body")
                        .tags(List.of("tag"))
                        .likesCount(0)
                        .commentsCount(0)
                        .build());
        String payload = objectMapper.writeValueAsString(new PostUpdatePayload("Updated", "New body", List.of("news", "java")));

        mockMvc.perform(put("/api/posts/{id}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.text").value("New body"))
                .andExpect(jsonPath("$.tags[0]").value("news"))
                .andExpect(jsonPath("$.tags[1]").value("java"));

        Post reloaded = postRepository.findById(post.getId()).orElseThrow();
        assertThat(reloaded.getTitle()).isEqualTo("Updated");
        assertThat(reloaded.getText()).isEqualTo("New body");
        assertThat(reloaded.getTags()).containsExactly("news", "java");
    }

    @Test
    void deletePost_removesPostAndComments() throws Exception {
        Post post = postRepository.save(
                Post.builder()
                        .title("Original")
                        .text("Body")
                        .tags(List.of())
                        .likesCount(0)
                        .commentsCount(1)
                        .build());
        commentRepository.save(
                Comment.builder()
                        .text("comment")
                        .post(post)
                        .build());

        mockMvc.perform(delete("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk());

        assertThat(postRepository.existsById(post.getId())).isFalse();
        assertThat(commentRepository.findByPostIdOrderByIdAsc(post.getId())).isEmpty();
    }

    @Test
    void updatePostImage_andRetrieveBytes() throws Exception {
        Post post = postRepository.save(
                Post.builder()
                        .title("Title")
                        .text("Text")
                        .tags(List.of())
                        .likesCount(0)
                        .commentsCount(0)
                        .build());
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png", new byte[]{1, 2, 3, 4});

        mockMvc.perform(multipart("/api/posts/{id}/image", post.getId())
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/posts/{id}/image", post.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andReturn();

        assertThat(result.getResponse().getContentAsByteArray()).containsExactly(1, 2, 3, 4);
    }

    @Test
    void createPost_withEmptyTitle_returnsBadRequest() throws Exception {
        String payload = objectMapper.writeValueAsString(new PostCreationPayload("   ", "Body", List.of()));

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("title must not be empty"));
    }

    @Test
    void updatePost_missingId_returnsBadRequest() throws Exception {
        PostUpdatePayload payload = new PostUpdatePayload(null, "Body", List.of());

        mockMvc.perform(put("/api/posts/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("id must be provided"));
    }

    @Test
    void updatePost_notFound_returns404() throws Exception {
        PostUpdatePayload payload = new PostUpdatePayload("Title", "Body", List.of());

        mockMvc.perform(put("/api/posts/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found: 9999"));
    }

    @Test
    void putImage_nonExistingPost_returns404() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png", new byte[]{1});

        mockMvc.perform(multipart("/api/posts/{id}/image", 9999)
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found: 9999"));
    }

    @Test
    void putImage_emptyFile_returnsBadRequest() throws Exception {
        Post post = postRepository.save(
                Post.builder()
                        .title("Title")
                        .text("Text")
                        .tags(List.of())
                        .likesCount(0)
                        .commentsCount(0)
                        .build());
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png", new byte[]{});

        mockMvc.perform(multipart("/api/posts/{id}/image", post.getId())
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Uploaded image must not be empty"));
    }

    @Test
    void getImage_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/posts/{id}/image", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found: 9999"));
    }

    private record PostCreationPayload(String title, String text, List<String> tags) {}

    private record PostUpdatePayload(String title, String text, List<String> tags) {}
}
