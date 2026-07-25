package com.tradewise.notes.service;

import com.tradewise.common.response.PagedResponse;
import com.tradewise.notes.dto.request.NoteFilter;
import com.tradewise.notes.dto.request.NoteRequest;
import com.tradewise.notes.dto.response.NoteResponse;
import com.tradewise.notes.dto.response.NoteVersionResponse;

import java.util.List;
import java.util.UUID;

/**
 * Notes use cases: CRUD, pin/favorite, archive/restore, search and version
 * history.
 */
public interface NoteService {

    NoteResponse create(UUID userId, NoteRequest request);

    NoteResponse update(UUID userId, UUID noteId, NoteRequest request);

    NoteResponse getById(UUID userId, UUID noteId);

    PagedResponse<NoteResponse> search(UUID userId, NoteFilter filter,
                                       int page, int size, String sortBy, String sortDir);

    void delete(UUID userId, UUID noteId);

    NoteResponse togglePin(UUID userId, UUID noteId);

    NoteResponse toggleFavorite(UUID userId, UUID noteId);

    NoteResponse archive(UUID userId, UUID noteId);

    NoteResponse restore(UUID userId, UUID noteId);

    List<NoteVersionResponse> getVersionHistory(UUID userId, UUID noteId);
}
