package com.tradewise.notes.dto.request;

import com.tradewise.notes.domain.enums.Mood;
import com.tradewise.notes.domain.enums.NoteCategory;

/**
 * Search / filter criteria for listing notes. All fields optional.
 */
public record NoteFilter(
        String search,
        NoteCategory category,
        Mood mood,
        String tag,
        Boolean pinned,
        Boolean favorite,
        Boolean archived
) {
}
