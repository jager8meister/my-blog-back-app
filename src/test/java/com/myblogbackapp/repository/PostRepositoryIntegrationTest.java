package com.myblogbackapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.myblogbackapp.AbstractIntegrationTest;
import com.myblogbackapp.entity.Post;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class PostRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase_filtersBySubstring() {
        Post matching = savePost("Mediterranean Cooking Guide", "Deep dive into herbs and spices", List.of("cooking"));
        Post nonMatching = savePost("Mountain Hiking Tips", "Packing list for alpine treks", List.of("hiking"));

        Page<Post> result = postRepository.findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase(
                "cooking",
                "cooking",
                PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Post::getId)
                .contains(matching.getId())
                .doesNotContain(nonMatching.getId());
    }

    @Test
    void findByAllTags_returnsOnlyPostsContainingEveryTag() {
        Post fullyTagged = savePost("Backpacking Southeast Asia", "Route samples", List.of("travel", "asia"));
        savePost("Backpacking Europe", "Notes", List.of("travel"));

        Page<Post> result = postRepository.findByAllTags(
                List.of("travel", "asia"),
                2,
                PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Post::getId)
                .containsExactly(fullyTagged.getId());
    }

    @Test
    void findBySearchTermAndAllTags_combinesFullTextAndTags() {
        Post expected = savePost("Sunset Yoga Retreat", "Relaxed breathing practice", List.of("wellness", "retreat"));
        savePost("Morning Yoga Routine", "Short practice", List.of("wellness"));
        savePost("Weekend Beach Picnic", "Sandwich ideas", List.of("picnic"));

        Page<Post> result = postRepository.findBySearchTermAndAllTags(
                "breathing",
                List.of("wellness", "retreat"),
                2,
                PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Post::getId)
                .containsExactly(expected.getId());
    }

    private Post savePost(String title, String text, List<String> tags) {
        return postRepository.saveAndFlush(
                Post.builder()
                        .title(title)
                        .text(text)
                        .tags(List.copyOf(tags))
                        .build()
        );
    }
}
