package com.tradewise.notes.controller;

import com.tradewise.common.response.ApiResponse;
import com.tradewise.common.response.PagedResponse;
import com.tradewise.notes.domain.enums.Mood;
import com.tradewise.notes.domain.enums.NoteCategory;
import com.tradewise.notes.dto.request.NoteFilter;
import com.tradewise.notes.dto.request.NoteRequest;
import com.tradewise.notes.dto.response.NoteResponse;
import com.tradewise.notes.dto.response.NoteVersionResponse;
import com.tradewise.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Notes endpoints: CRUD, pin/favorite, archive/restore, search and versions.
 */
@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "Trading notes and journal with version history")
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "Create a note")
    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> create(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(noteService.create(userId, request), "Note created"));
    }

    @Operation(summary = "Update a note")
    @PutMapping("/{noteId}")
    public ResponseEntity<ApiResponse<NoteResponse>> update(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID noteId,
            @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(noteService.update(userId, noteId, request), "Note updated"));
    }

    @Operation(summary = "Get a note by id")
    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponse<NoteResponse>> get(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID noteId) {
        return ResponseEntity.ok(ApiResponse.success(noteService.getById(userId, noteId)));
    }

    @Operation(summary = "Search / filter / paginate notes")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<NoteResponse>>> search(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) NoteCategory category,
            @RequestParam(required = false) Mood mood,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        NoteFilter filter = new NoteFilter(search, category, mood, tag, pinned, favorite, archived);
        return ResponseEntity.ok(ApiResponse.success(
                noteService.search(userId, filter, page, size, sortBy, sortDir)));
    }

    @Operation(summary = "Delete a note (soft delete)")
    @DeleteMapping("/{noteId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID noteId) {
        noteService.delete(userId, noteId);
        return ResponseEntity.ok(ApiResponse.success(null, "Note deleted"));
    }

    @Operation(summary = "Toggle pin")
    @PatchMapping("/{noteId}/pin")
    public ResponseEntity<ApiResponse<NoteResponse>> togglePin(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID noteId) {
        return ResponseEntity.ok(ApiResponse.success(noteService.togglePin(userId, noteId)));
    }

    @Operation(summary = "Toggle favorite")
    @PatchMapping("/{noteId}/favorite")
    public ResponseEntity<ApiResponse<NoteResponse>> toggleFavorite(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID noteId) {
        return ResponseEntity.ok(ApiResponse.success(noteService.toggleFavorite(userId, noteId)));
    }

    @Operation(summary = "Archive a note")
    @PatchMapping("/{noteId}/archive")
    public ResponseEntity<ApiResponse<NoteResponse>> archive(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID noteId) {
        return ResponseEntity.ok(ApiResponse.success(noteService.archive(userId, noteId), "Note archived"));
    }

    @Operation(summary = "Restore an archived note")
    @PatchMapping("/{noteId}/restore")
    public ResponseEntity<ApiResponse<NoteResponse>> restore(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID noteId) {
        return ResponseEntity.ok(ApiResponse.success(noteService.restore(userId, noteId), "Note restored"));
    }

    @Operation(summary = "Get a note's version history")
    @GetMapping("/{noteId}/versions")
    public ResponseEntity<ApiResponse<List<NoteVersionResponse>>> versions(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID noteId) {
        return ResponseEntity.ok(ApiResponse.success(noteService.getVersionHistory(userId, noteId)));
    }
}
