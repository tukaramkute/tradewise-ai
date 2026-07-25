package com.tradewise.notes.domain;

import com.tradewise.common.domain.BaseEntity;
import com.tradewise.notes.domain.enums.Mood;
import com.tradewise.notes.domain.enums.NoteCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A trading note / journal entry. Supports rich-text/markdown content, tagging,
 * mood + rating, pin/favorite, archive/restore and version history.
 */
@Getter
@Setter
@NoArgsConstructor
public class Note extends BaseEntity {

    private UUID userId;

    private String title;

    /** Markdown / rich-text content. */
    private String content;

    private NoteCategory category;
    private Mood mood;

    /** 1–5 self rating; null if unset. */
    private Integer rating;

    private Set<String> tags = new LinkedHashSet<>();
    private List<String> attachments = new ArrayList<>();

    private boolean pinned = false;
    private boolean favorite = false;
    private boolean archived = false;

    private List<NoteVersion> versionHistory = new ArrayList<>();
}
