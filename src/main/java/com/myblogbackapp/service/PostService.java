package com.myblogbackapp.service;

import com.myblogbackapp.dto.request.CreatePostRequestDto;
import com.myblogbackapp.dto.request.UpdatePostRequestDto;
import com.myblogbackapp.dto.response.PostImageResponseDto;
import com.myblogbackapp.dto.response.PostResponseDto;
import com.myblogbackapp.dto.response.PostsResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    PostResponseDto createPost(CreatePostRequestDto request);

    void updatePostImage(Long postId, MultipartFile imageFile);

    PostImageResponseDto getPostImage(Long postId);

    PostResponseDto getPost(Long postId);

    PostsResponseDto getPosts(String search, int pageNumber, int pageSize);

    PostResponseDto updatePost(Long id, UpdatePostRequestDto request);

    void deletePost(Long postId);
}
