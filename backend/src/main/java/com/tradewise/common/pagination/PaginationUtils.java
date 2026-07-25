package com.tradewise.common.pagination;

import java.util.Comparator;
import java.util.List;

/**
 * Utility for applying sort + pagination to in-memory lists, plus building a
 * {@link com.tradewise.common.response.PagedResponse}. Centralises the slicing
 * logic so every feature paginates consistently.
 */
public final class PaginationUtils {

    private PaginationUtils() {
    }

    /** Returns a defensive, sorted, sliced sublist for the requested page. */
    public static <T> List<T> paginate(List<T> source, int page, int size, Comparator<T> comparator) {
        List<T> sorted = source.stream()
                .sorted(comparator == null ? nullComparator() : comparator)
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        int from = Math.min(safePage * safeSize, sorted.size());
        int to = Math.min(from + safeSize, sorted.size());
        return List.copyOf(sorted.subList(from, to));
    }

    @SuppressWarnings("unchecked")
    private static <T> Comparator<T> nullComparator() {
        return (Comparator<T>) Comparator.comparingInt(Object::hashCode);
    }
}
