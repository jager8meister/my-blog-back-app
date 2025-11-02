package com.myblogbackapp.service.impl;

import com.myblogbackapp.dto.request.CreatePostRequestDto;
import com.myblogbackapp.dto.request.UpdatePostRequestDto;
import com.myblogbackapp.dto.response.PostImageResponseDto;
import com.myblogbackapp.dto.response.PostResponseDto;
import com.myblogbackapp.dto.response.PostsResponseDto;
import com.myblogbackapp.entity.Comment;
import com.myblogbackapp.entity.Post;
import com.myblogbackapp.exception.PostIdMissingException;
import com.myblogbackapp.exception.PostImageEmptyException;
import com.myblogbackapp.exception.PostImageNotFoundException;
import com.myblogbackapp.exception.PostImageProcessingException;
import com.myblogbackapp.exception.PostNotFoundException;
import com.myblogbackapp.exception.PostTextEmptyException;
import com.myblogbackapp.exception.PostTitleEmptyException;
import com.myblogbackapp.mapper.PostMapper;
import com.myblogbackapp.repository.CommentRepository;
import com.myblogbackapp.repository.PostRepository;
import com.myblogbackapp.service.PostService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;

    @Override
    public PostResponseDto createPost(CreatePostRequestDto request) {
        log.info("Creating post with title='{}'", request != null ? request.getTitle() : null);
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new PostTitleEmptyException("title must not be empty");
        }
        if (request.getText() == null || request.getText().isBlank()) {
            throw new PostTextEmptyException("text must not be empty");
        }
        Post post = postMapper.toEntity(request);

        Post saved = postRepository.saveAndFlush(post);
        log.info("Post created with id={}", saved.getId());
        return postMapper.toResponse(saved);
    }

    @Override
    public void updatePostImage(Long postId, MultipartFile imageFile) {
        log.info("Updating image for post {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));

        if (imageFile == null || imageFile.isEmpty()) {
            throw new PostImageEmptyException("Uploaded image must not be empty");
        }

        try {
            post.setImageData(imageFile.getBytes());
        } catch (IOException e) {
            throw new PostImageProcessingException("Failed to read uploaded image");
        }
        post.setImageContentType(imageFile.getContentType());
        post.setImageFilename(imageFile.getOriginalFilename());

        postRepository.saveAndFlush(post);
        log.info("Image updated for post {}", postId);
    }

    @Override
    public PostImageResponseDto getPostImage(Long postId) {
        log.info("Fetching image for post {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));

        byte[] data = post.getImageData();
        if (data == null || data.length == 0) {
            throw new PostImageNotFoundException("Image not found for post: " + postId);
        }

        return new PostImageResponseDto(
                data,
                post.getImageContentType(),
                post.getImageFilename()
        );
    }

    @Override
    public PostResponseDto getPost(Long postId) {
        log.info("Fetching post {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
        return postMapper.toResponse(post);
    }

    @Override
    public PostsResponseDto getPosts(String search, int pageNumber, int pageSize) {
        log.info("Fetching posts with search='{}', pageNumber={}, pageSize={}", search, pageNumber, pageSize);
        int safePageSize = Math.max(1, pageSize);
        int safePageNumber = Math.max(1, pageNumber) - 1;
        Pageable pageable = PageRequest.of(
                safePageNumber,
                safePageSize,
                Sort.by(Sort.Direction.ASC, "id")
        );

        String normalized = search == null ? "" : search.trim();

        List<String> tokens = normalized.isBlank()
                ? List.of()
                : java.util.Arrays.stream(normalized.split("\\s+"))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .toList();

        List<String> tagFilters = tokens.stream()
                .filter(token -> token.startsWith("#"))
                .map(token -> token.substring(1))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .toList();

        List<String> textParts = tokens.stream()
                .filter(token -> !token.startsWith("#"))
                .toList();

        String substring = textParts.isEmpty() ? "" : String.join(" ", textParts);

        Page<Post> page;
        if (tagFilters.isEmpty() && substring.isBlank()) {
            page = postRepository.findAll(pageable);
        } else if (tagFilters.isEmpty()) {
            page = postRepository.findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase(substring, substring, pageable);
        } else {
            page = substring.isBlank() ?
                    postRepository.findByAllTags(tagFilters, tagFilters.size(), pageable) :
                    postRepository.findBySearchTermAndAllTags(substring, tagFilters, tagFilters.size(), pageable);
        }

        List<PostResponseDto> postDtos = page.getContent().isEmpty()
                ? List.of()
                : postMapper.toResponseList(page.getContent());

        if (postDtos.isEmpty()) {
            return new PostsResponseDto(List.of(), false, false, 0);
        }

        return new PostsResponseDto(
                postDtos,
                page.hasPrevious(),
                page.hasNext(),
                page.getTotalPages()
        );
    }

    @Override
    public PostResponseDto updatePost(Long id, UpdatePostRequestDto request) {
        log.info("Updating post {}", id);
        if (id == null || id <= 0) {
            throw new PostIdMissingException("id must be provided");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new PostTitleEmptyException("title must not be empty");
        }
        if (request.getText() == null || request.getText().isBlank()) {
            throw new PostTextEmptyException("text must not be empty");
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + id));

        postMapper.updateEntity(request, post);

        Post updated = postRepository.saveAndFlush(post);
        log.info("Post {} updated", id);
        return postMapper.toResponse(updated);
    }

    @Override
    public void deletePost(Long postId) {
        log.info("Deleting post {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));

        List<Comment> comments = commentRepository.findByPostIdOrderByIdAsc(postId);
        commentRepository.deleteAll(comments);

        postRepository.delete(post);
        log.info("Post {} deleted", postId);
    }
}
