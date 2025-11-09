package com.myblogbackapp.mapper;

import com.myblogbackapp.dto.request.CreateCommentRequestDto;
import com.myblogbackapp.dto.request.UpdateCommentRequestDto;
import com.myblogbackapp.dto.response.PostCommentResponseDto;
import com.myblogbackapp.entity.Comment;
import com.myblogbackapp.entity.Post;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    PostCommentResponseDto toResponse(Comment comment);

    List<PostCommentResponseDto> toResponseList(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "request.text", qualifiedByName = "trim")
    @Mapping(target = "post", source = "post")
    Comment toEntity(CreateCommentRequestDto request, Post post);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "text", source = "request.text", qualifiedByName = "trim")
    void updateEntity(UpdateCommentRequestDto request, @MappingTarget Comment comment);

    @Named("trim")
    default String trim(String value) {
        return value == null ? null : value.trim();
    }
}
