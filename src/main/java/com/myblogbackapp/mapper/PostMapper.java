package com.myblogbackapp.mapper;

import com.myblogbackapp.dto.request.CreatePostRequestDto;
import com.myblogbackapp.dto.request.UpdatePostRequestDto;
import com.myblogbackapp.dto.response.PostResponseDto;
import com.myblogbackapp.entity.Post;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponseDto toResponse(Post post);

    List<PostResponseDto> toResponseList(List<Post> posts);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "normalizeTags")
    Post toEntity(CreatePostRequestDto request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title",  qualifiedByName = "trim")
    @Mapping(target = "text", qualifiedByName = "trim")
    void updateEntity(UpdatePostRequestDto request, @MappingTarget Post post);

    @AfterMapping
    default void afterUpdate(UpdatePostRequestDto request, @MappingTarget Post post) {
        post.setTags(normalizeTags(request.getTags()));
    }

    @Named("trim")
    default String trim(String value) {
        return value == null ? null : value.trim();
    }

    @Named("normalizeTags")
    default List<String> normalizeTags(List<String> tags) {
        return tags == null ? new ArrayList<>() : new ArrayList<>(tags);
    }
}
