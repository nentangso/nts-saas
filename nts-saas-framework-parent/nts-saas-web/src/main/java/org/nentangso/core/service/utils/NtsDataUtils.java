package org.nentangso.core.service.utils;

import org.nentangso.core.config.NtsConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SuppressWarnings("unused")
public class NtsDataUtils {
    private NtsDataUtils() {
    }

    public static Pageable pageableWithDefaultSort(Pageable pageable, Sort sort) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        return PageRequest.of(
            Math.max(page, 0),
            size > 0 && size <= NtsConstants.MAX_ITEMS_PER_PAGE ? size : NtsConstants.ITEMS_PER_PAGE,
            pageable.getSortOr(sort)
        );
    }
}
