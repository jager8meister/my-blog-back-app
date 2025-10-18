package com.myblogbackapp.mapper;

import com.myblogbackapp.dto.response.PostResponseDto;
import com.myblogbackapp.entity.Post;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostResponseDto toResponse(Post post) {
        if (post == null) {
            return null;
        }
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getText(),
                post.getTags(),
                post.getLikesCount(),
                post.getCommentsCount()
        );
    }

    public List<PostResponseDto> toResponseList(List<Post> posts) {
        return posts.stream()
                .map(this::toResponse)
                .toList();
    }
}

