package com.tradewise.notes.dto.response;

import java.time.Instant;

public record NoteVersionResponse(
        int version,
        String title,
        String content,
        Instant capturedAt
) {
}
