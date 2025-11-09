package com.myblogbackapp.service.helper;

import com.myblogbackapp.service.model.PostSearchCriteria;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PostSearchCriteriaFactory {

    public PostSearchCriteria fromRawSearch(String rawSearch) {
        String normalized = rawSearch == null ? "" : rawSearch.trim();
        if (normalized.isBlank()) {
            return new PostSearchCriteria("", List.of());
        }

        List<String> tokens = Arrays.stream(normalized.split("\\s+"))
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

        return new PostSearchCriteria(substring, tagFilters);
    }
}
