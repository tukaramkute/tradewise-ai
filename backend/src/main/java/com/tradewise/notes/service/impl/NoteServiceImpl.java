package com.tradewise.notes.service.impl;

import com.tradewise.common.exception.ResourceNotFoundException;
import com.tradewise.common.pagination.PaginationUtils;
import com.tradewise.common.response.PagedResponse;
import com.tradewise.common.security.CurrentUserProvider;
import com.tradewise.notes.domain.Note;
import com.tradewise.notes.domain.NoteVersion;
import com.tradewise.notes.dto.request.NoteFilter;
import com.tradewise.notes.dto.request.NoteRequest;
import com.tradewise.notes.dto.response.NoteResponse;
import com.tradewise.notes.dto.response.NoteVersionResponse;
import com.tradewise.notes.mapper.NoteMapper;
import com.tradewise.notes.repository.NoteRepository;
import com.tradewise.notes.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 * Default {@link NoteService}. Captures a version snapshot on every content
 * change so the full edit history is preserved.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public NoteResponse create(UUID userId, NoteRequest request) {
        Note note = new Note();
        note.setUserId(userId);
        apply(note, request);
        note.setPinned(Boolean.TRUE.equals(request.pinned()));
        note.setFavorite(Boolean.TRUE.equals(request.favorite()));
        note.getVersionHistory().add(snapshot(note, 1));
        stampCreate(note);

        Note saved = noteRepository.save(note);
        log.info("Note created id={} user={}", saved.getId(), userId);
        return noteMapper.toResponse(saved);
    }

    @Override
    public NoteResponse update(UUID userId, UUID noteId, NoteRequest request) {
        Note note = getOwned(userId, noteId);
        boolean contentChanged = !java.util.Objects.equals(note.getTitle(), request.title())
                || !java.util.Objects.equals(note.getContent(), request.content());

        apply(note, request);
        if (request.pinned() != null) {
            note.setPinned(request.pinned());
        }
        if (request.favorite() != null) {
            note.setFavorite(request.favorite());
        }
        if (contentChanged) {
            note.getVersionHistory().add(snapshot(note, note.getVersionHistory().size() + 1));
        }
        stampUpdate(note);
        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponse getById(UUID userId, UUID noteId) {
        return noteMapper.toResponse(getOwned(userId, noteId));
    }

    @Override
    public PagedResponse<NoteResponse> search(UUID userId, NoteFilter filter,
                                              int page, int size, String sortBy, String sortDir) {
        List<Note> matched = noteRepository.search(userId, filter);
        Comparator<Note> comparator = buildComparator(sortBy, sortDir);
        List<Note> pageItems = PaginationUtils.paginate(matched, page, size, comparator);
        List<NoteResponse> content = pageItems.stream().map(noteMapper::toResponse).toList();
        return PagedResponse.of(content, page, size, matched.size());
    }

    @Override
    public void delete(UUID userId, UUID noteId) {
        Note note = getOwned(userId, noteId);
        note.softDelete(currentUserProvider.getCurrentUsername());
        noteRepository.save(note);
        log.info("Note soft-deleted id={}", noteId);
    }

    @Override
    public NoteResponse togglePin(UUID userId, UUID noteId) {
        Note note = getOwned(userId, noteId);
        note.setPinned(!note.isPinned());
        stampUpdate(note);
        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponse toggleFavorite(UUID userId, UUID noteId) {
        Note note = getOwned(userId, noteId);
        note.setFavorite(!note.isFavorite());
        stampUpdate(note);
        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponse archive(UUID userId, UUID noteId) {
        Note note = getOwned(userId, noteId);
        note.setArchived(true);
        stampUpdate(note);
        log.info("Note archived id={}", noteId);
        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponse restore(UUID userId, UUID noteId) {
        Note note = getOwned(userId, noteId);
        note.setArchived(false);
        stampUpdate(note);
        log.info("Note restored id={}", noteId);
        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Override
    public List<NoteVersionResponse> getVersionHistory(UUID userId, UUID noteId) {
        Note note = getOwned(userId, noteId);
        return note.getVersionHistory().stream()
                .sorted(Comparator.comparingInt(NoteVersion::getVersion).reversed())
                .map(noteMapper::toVersionResponse)
                .toList();
    }

    // ----- helpers -----

    private Note getOwned(UUID userId, UUID noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> !n.isDeleted())
                .orElseThrow(() -> ResourceNotFoundException.of("Note", noteId));
        if (!note.getUserId().equals(userId)) {
            throw ResourceNotFoundException.of("Note", noteId);
        }
        return note;
    }

    private void apply(Note note, NoteRequest request) {
        note.setTitle(request.title());
        note.setContent(request.content());
        note.setCategory(request.category());
        note.setMood(request.mood());
        note.setRating(request.rating());
        note.setTags(request.tags() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(request.tags()));
        if (request.attachments() != null) {
            note.setAttachments(new java.util.ArrayList<>(request.attachments()));
        }
    }

    private NoteVersion snapshot(Note note, int version) {
        return new NoteVersion(version, note.getTitle(), note.getContent(), Instant.now());
    }

    private Comparator<Note> buildComparator(String sortBy, String sortDir) {
        Comparator<Note> comparator = switch (sortBy == null ? "updatedDate" : sortBy) {
            case "title" -> Comparator.comparing(Note::getTitle, Comparator.nullsLast(Comparator.naturalOrder()));
            case "createdDate" -> Comparator.comparing(Note::getCreatedDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "rating" -> Comparator.comparing(Note::getRating, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(Note::getUpdatedDate, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        Comparator<Note> directed = "asc".equalsIgnoreCase(sortDir) ? comparator : comparator.reversed();
        // Pinned notes always float to the top.
        return Comparator.comparing(Note::isPinned).reversed().thenComparing(directed);
    }

    private void stampCreate(Note note) {
        String actor = currentUserProvider.getCurrentUsername();
        Instant now = Instant.now();
        note.setCreatedBy(actor);
        note.setCreatedDate(now);
        note.setUpdatedBy(actor);
        note.setUpdatedDate(now);
    }

    private void stampUpdate(Note note) {
        note.setUpdatedBy(currentUserProvider.getCurrentUsername());
        note.setUpdatedDate(Instant.now());
    }
}
