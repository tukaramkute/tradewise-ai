package com.tradewise.notes.mapper;

import com.tradewise.notes.domain.Note;
import com.tradewise.notes.domain.NoteVersion;
import com.tradewise.notes.dto.response.NoteResponse;
import com.tradewise.notes.dto.response.NoteVersionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "versionCount", expression = "java(note.getVersionHistory().size())")
    NoteResponse toResponse(Note note);

    NoteVersionResponse toVersionResponse(NoteVersion version);
}
