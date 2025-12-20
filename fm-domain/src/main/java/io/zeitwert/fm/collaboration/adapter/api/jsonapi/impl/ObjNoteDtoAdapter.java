package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import org.springframework.stereotype.Component;

@Component("objNoteDtoAdapter")
public class ObjNoteDtoAdapter extends ObjDtoAdapterBase<ObjNote, ObjNoteDto> {

	@Override
	public void toAggregate(ObjNoteDto dto, ObjNote note) {
		super.toAggregate(dto, note);
		note.setRelatedToId(Integer.parseInt(dto.getRelatedToId()));
		note.setNoteType(dto.getNoteType() == null ? null : CodeNoteType.getNoteType(dto.getNoteType().getId()));
		note.setSubject(dto.getSubject());
		note.setContent(dto.getContent());
		note.setPrivate(dto.getIsPrivate());
	}

	@Override
	public ObjNoteDto fromAggregate(ObjNote note) {
		if (note == null) {
			return null;
		}
		ObjNoteDto.ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder();
		this.fromAggregate(dtoBuilder, note);
		// @formatter:off
		return dtoBuilder
			.relatedToId(note.getRelatedToId().toString())
			.noteType(EnumeratedDto.of(note.getNoteType()))
			.subject(note.getSubject())
			.content(note.getContent())
			.isPrivate(note.isPrivate())
			.build();
		// @formatter:on
	}

//	@Override
//	public ObjNoteDto fromRecord(ObjNoteVRecord note) {
//		if (note == null) {
//			return null;
//		}
//		ObjNoteDto.ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder();
//		this.fromRecord(dtoBuilder, note);
//		// @formatter:off
//		return dtoBuilder
//			.relatedToId(note.getRelatedToId().toString())
//			.noteType(EnumeratedDto.of(CodeNoteType.getNoteType(note.getNoteTypeId())))
//			.subject(note.getSubject())
//			.content(note.getContent())
//			.isPrivate(note.getIsPrivate())
//			.build();
//		// @formatter:on
//	}

}
