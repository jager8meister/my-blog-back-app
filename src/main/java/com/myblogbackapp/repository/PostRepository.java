package com.myblogbackapp.repository;

import com.myblogbackapp.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase(String title, String text, Pageable pageable);

    @Query(
            value = """
                    SELECT p FROM Post p
                    WHERE p.id IN (
                        SELECT p2.id FROM Post p2
                        JOIN p2.tags tag
                        WHERE LOWER(tag) IN :tags
                        GROUP BY p2.id
                        HAVING COUNT(DISTINCT LOWER(tag)) = :tagCount
                    )
                    """,
            countQuery = """
                    SELECT COUNT(p) FROM Post p
                    WHERE p.id IN (
                        SELECT p2.id FROM Post p2
                        JOIN p2.tags tag
                        WHERE LOWER(tag) IN :tags
                        GROUP BY p2.id
                        HAVING COUNT(DISTINCT LOWER(tag)) = :tagCount
                    )
                    """
    )
    Page<Post> findByAllTags(@Param("tags") List<String> tags, @Param("tagCount") long tagCount, Pageable pageable);

    @Query(
            value = """
                    SELECT p FROM Post p
                    WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :substring, '%'))
                       OR LOWER(p.text) LIKE LOWER(CONCAT('%', :substring, '%')))
                      AND p.id IN (
                        SELECT p2.id FROM Post p2
                        JOIN p2.tags tag
                        WHERE LOWER(tag) IN :tags
                        GROUP BY p2.id
                        HAVING COUNT(DISTINCT LOWER(tag)) = :tagCount
                    )
                    """,
            countQuery = """
                    SELECT COUNT(p) FROM Post p
                    WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :substring, '%'))
                       OR LOWER(p.text) LIKE LOWER(CONCAT('%', :substring, '%')))
                      AND p.id IN (
                        SELECT p2.id FROM Post p2
                        JOIN p2.tags tag
                        WHERE LOWER(tag) IN :tags
                        GROUP BY p2.id
                        HAVING COUNT(DISTINCT LOWER(tag)) = :tagCount
                    )
                    """
    )
    Page<Post> findBySearchTermAndAllTags(@Param("substring") String substring,
                                          @Param("tags") List<String> tags,
                                          @Param("tagCount") long tagCount,
                                          Pageable pageable);
}
