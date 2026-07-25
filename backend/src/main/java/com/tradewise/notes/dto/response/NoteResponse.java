package com.tradewise.notes.dto.response;

import com.tradewise.notes.domain.enums.Mood;
import com.tradewise.notes.domain.enums.NoteCategory;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        UUID userId,
        String title,
        String content,
        NoteCategory category,
        Mood mood,
        Integer rating,
        Set<String> tags,
        List<String> attachments,
        boolean pinned,
        boolean favorite,
        boolean archived,
        int versionCount,
        Instant createdDate,
        Instant updatedDate
) {
}
