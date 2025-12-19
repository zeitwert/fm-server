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
		note.relatedToId = Integer.parseInt(dto.getRelatedToId());
		note.noteType = dto.getNoteType() == null ? null : CodeNoteType.getNoteType(dto.getNoteType().getId());
		note.subject = dto.getSubject();
		note.content = dto.getContent();
		note.isPrivate = dto.getIsPrivate();
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
			.relatedToId(note.relatedToId.toString())
			.noteType(EnumeratedDto.of(note.noteType))
			.subject(note.subject)
			.content(note.content)
			.isPrivate(note.isPrivate)
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
