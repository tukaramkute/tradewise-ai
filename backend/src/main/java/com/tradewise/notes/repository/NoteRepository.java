package com.tradewise.notes.repository;

import com.tradewise.common.repository.CrudRepository;
import com.tradewise.notes.domain.Note;
import com.tradewise.notes.dto.request.NoteFilter;

import java.util.List;
import java.util.UUID;

public interface NoteRepository extends CrudRepository<Note> {

    List<Note> findAllByUserId(UUID userId);

    List<Note> search(UUID userId, NoteFilter filter);
}
