package com.tradewise.common.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

/**
 * Pagination-aware wrapper returned by list endpoints that support paging.
 *
 * <p>Self-contained (no Spring Data dependency) so it works with the in-memory
 * repositories. Later, a Spring Data {@code Page} can be adapted to this shape
 * without changing the API contract.</p>
 *
 * @param <T> element type
 */
@Getter
@Builder
public class PagedResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final boolean empty;

    /**
     * Builds a page from an already-sliced content list plus paging metadata.
     */
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return PagedResponse.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page <= 0)
                .last(page >= totalPages - 1)
                .empty(content.isEmpty())
                .build();
    }

    /**
     * Maps the content of an existing page to another type, preserving metadata.
     */
    public <R> PagedResponse<R> map(Function<T, R> mapper) {
        return PagedResponse.<R>builder()
                .content(content.stream().map(mapper).toList())
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(first)
                .last(last)
                .empty(empty)
                .build();
    }
}
