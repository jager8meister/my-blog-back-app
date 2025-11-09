package com.myblogbackapp.service.helper;

import com.myblogbackapp.entity.Post;
import com.myblogbackapp.repository.PostRepository;
import com.myblogbackapp.service.model.PostSearchCriteria;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostPageProvider {

    private final PostRepository postRepository;

    public Page<Post> fetch(PostSearchCriteria criteria, Pageable pageable) {
        boolean hasTags = criteria.hasTagFilters();
        boolean hasSearchTerm = criteria.hasSearchTerm();
        String searchTerm = criteria.searchTerm();
        List<String> tags = criteria.tagFilters();

        if (!hasTags && !hasSearchTerm) {
            return postRepository.findAll(pageable);
        }
        if (!hasTags) {
            return postRepository.findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase(
                    searchTerm,
                    searchTerm,
                    pageable
            );
        }
        if (!hasSearchTerm) {
            return postRepository.findByAllTags(tags, tags.size(), pageable);
        }
        return postRepository.findBySearchTermAndAllTags(searchTerm, tags, tags.size(), pageable);
    }
}
