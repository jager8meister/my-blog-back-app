package com.myblogbackapp.controller;

import com.myblogbackapp.dto.request.CreatePostRequestDto;
import com.myblogbackapp.dto.request.UpdatePostRequestDto;
import com.myblogbackapp.dto.response.PostImageResponseDto;
import com.myblogbackapp.dto.response.PostResponseDto;
import com.myblogbackapp.dto.response.PostsResponseDto;
import com.myblogbackapp.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Posts", description = "Operations with blog posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PutMapping(value = "/api/posts/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload or replace post image")
    public void updatePostImage(
            @Parameter(description = "Post identifier", required = true) @PathVariable("id") Long id,

            @Parameter(description = "Image file") @RequestParam("image") MultipartFile image
    ) {
        postService.updatePostImage(id, image);
    }

    @PostMapping("/api/posts")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new post")
    public PostResponseDto createPost(@RequestBody CreatePostRequestDto request) {
        return postService.createPost(request);
    }

    @PutMapping("/api/posts/{id}")
    @Operation(summary = "Update post fields")
    public PostResponseDto updatePost(
            @Parameter(description = "Post identifier", required = true) @PathVariable("id") Long id,

            @RequestBody UpdatePostRequestDto request
    ) {
        return postService.updatePost(id, request);
    }

    @GetMapping("/api/posts")
    @Operation(summary = "List posts with search and pagination")
    public PostsResponseDto listPosts(
            @Parameter(description = "Search string (supports #tag syntax)", example = "#spring tips")
            @RequestParam(value = "search", required = false, defaultValue = "") String search,

            @Parameter(description = "Page number starting from 1", example = "1")
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,

            @Parameter(description = "Page size", example = "5")
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") int pageSize
    ) {
        return postService.getPosts(search, pageNumber, pageSize);
    }

    @GetMapping({"/api/posts/{id}", "/posts/{id}"})
    @Operation(summary = "Get single post by id")
    public PostResponseDto getPost(@Parameter(description = "Post identifier", required = true) @PathVariable("id") Long id) {
        return postService.getPost(id);
    }

    @DeleteMapping("/api/posts/{id}")
    @Operation(summary = "Delete post together with its comments")
    public void deletePost(@Parameter(description = "Post identifier", required = true) @PathVariable("id") Long id) {
        postService.deletePost(id);
    }

    @GetMapping("/api/posts/{id}/image")
    @Operation(summary = "Download post image")
    public ResponseEntity<byte[]> getPostImage(@Parameter(description = "Post identifier", required = true) @PathVariable("id") Long id) {
        PostImageResponseDto image = postService.getPostImage(id);
        String contentType = image.getContentType();
        MediaType mediaType = MediaType.parseMediaType(contentType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        String filename = image.getFilename();
        if (filename != null && !filename.isBlank()) {
            headers.setContentDisposition(ContentDisposition.inline().filename(filename).build());
        }
        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }
}
