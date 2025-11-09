package com.myblogbackapp.repository;

import com.myblogbackapp.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "post")
    List<Comment> findByPostIdOrderByIdAsc(Long postId);
}
