package com.tradewise.notes.dto.request;

import com.tradewise.notes.domain.enums.Mood;
import com.tradewise.notes.domain.enums.NoteCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

/**
 * Create / update a note. Content supports markdown / rich text.
 */
public record NoteRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 150)
        String title,

        @NotBlank(message = "Content is required")
        String content,

        NoteCategory category,
        Mood mood,

        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        Integer rating,

        Set<String> tags,
        List<String> attachments,

        Boolean pinned,
        Boolean favorite
) {
}
