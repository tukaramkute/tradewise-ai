package com.tradewise.notes.repository;

import com.tradewise.common.repository.InMemoryCrudRepository;
import com.tradewise.notes.domain.Note;
import com.tradewise.notes.dto.request.NoteFilter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Repository
public class InMemoryNoteRepository extends InMemoryCrudRepository<Note> implements NoteRepository {

    @Override
    public List<Note> findAllByUserId(UUID userId) {
        return findActiveBy(n -> userId.equals(n.getUserId()));
    }

    @Override
    public List<Note> search(UUID userId, NoteFilter filter) {
        Predicate<Note> predicate = n -> userId.equals(n.getUserId());
        if (filter != null) {
            predicate = predicate.and(matches(filter));
        }
        return findActiveBy(predicate);
    }

    private Predicate<Note> matches(NoteFilter f) {
        Predicate<Note> p = n -> true;
        if (f.search() != null && !f.search().isBlank()) {
            String q = f.search().toLowerCase();
            p = p.and(n -> (n.getTitle() != null && n.getTitle().toLowerCase().contains(q))
                    || (n.getContent() != null && n.getContent().toLowerCase().contains(q)));
        }
        if (f.category() != null) {
            p = p.and(n -> f.category() == n.getCategory());
        }
        if (f.mood() != null) {
            p = p.and(n -> f.mood() == n.getMood());
        }
        if (f.tag() != null && !f.tag().isBlank()) {
            p = p.and(n -> n.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(f.tag())));
        }
        if (f.pinned() != null) {
            p = p.and(n -> n.isPinned() == f.pinned());
        }
        if (f.favorite() != null) {
            p = p.and(n -> n.isFavorite() == f.favorite());
        }
        // Default: exclude archived unless explicitly requested.
        if (f.archived() != null) {
            p = p.and(n -> n.isArchived() == f.archived());
        } else {
            p = p.and(n -> !n.isArchived());
        }
        return p;
    }
}
