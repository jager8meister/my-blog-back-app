package com.myblogbackapp.service.impl;

import com.myblogbackapp.entity.Post;
import com.myblogbackapp.exception.PostNotFoundException;
import com.myblogbackapp.repository.PostRepository;
import com.myblogbackapp.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;

    @Override
    public int incrementLikes(Long postId) {
        log.info("Incrementing likes for post {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));

        post.setLikesCount(post.getLikesCount() + 1);
        Post saved = postRepository.save(post);
        log.info("Likes counter now {} for post {}", saved.getLikesCount(), postId);
        return saved.getLikesCount();
    }
}
