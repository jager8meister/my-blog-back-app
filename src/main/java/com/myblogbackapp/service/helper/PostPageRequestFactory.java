package com.myblogbackapp.service.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PostPageRequestFactory {

    public Pageable create(int pageNumber, int pageSize) {
        int safePageSize = Math.max(1, pageSize);
        int safePageNumber = Math.max(1, pageNumber) - 1;

        return PageRequest.of(
                safePageNumber,
                safePageSize,
                Sort.by(Sort.Direction.ASC, "id")
        );
    }
}
