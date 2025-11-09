package com.myblogbackapp.service.model;

import java.util.List;

public record PostSearchCriteria(String searchTerm, List<String> tagFilters) {

    public boolean hasSearchTerm() {
        return searchTerm != null && !searchTerm.isBlank();
    }

    public boolean hasTagFilters() {
        return tagFilters != null && !tagFilters.isEmpty();
    }
}
