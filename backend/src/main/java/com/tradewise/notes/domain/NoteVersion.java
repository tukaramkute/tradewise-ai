package com.tradewise.notes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Immutable-ish snapshot of a note's content captured for version history.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteVersion {

    private int version;
    private String title;
    private String content;
    private Instant capturedAt;
}
