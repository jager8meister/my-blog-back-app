package com.myblogbackapp.service.helper;

import com.myblogbackapp.dto.response.PostResponseDto;
import com.myblogbackapp.dto.response.PostsResponseDto;
import com.myblogbackapp.entity.Post;
import com.myblogbackapp.mapper.PostMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostsResponseFactory {

    private final PostMapper postMapper;

    public PostsResponseDto fromPage(Page<Post> page) {
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
}
