package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Component;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;

@Component("objNoteDtoAdapter")
public class ObjNoteDtoAdapter extends ObjDtoAdapterBase<ObjNote, ObjNoteVRecord, ObjNoteDto> {

	@Override
	public void toAggregate(ObjNoteDto dto, ObjNote note) {
		super.toAggregate(dto, note);
		note.setRelatedToId(Integer.parseInt(dto.getRelatedToId()));
		note.setNoteType(dto.getNoteType() == null ? null : CodeNoteTypeEnum.getNoteType(dto.getNoteType().getId()));
		note.setSubject(dto.getSubject());
		note.setContent(dto.getContent());
		note.setIsPrivate(dto.getIsPrivate());
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
			.noteType(EnumeratedDto.fromEnum(note.getNoteType()))
			.subject(note.getSubject())
			.content(note.getContent())
			.isPrivate(note.getIsPrivate())
			.build();
		// @formatter:on
	}

	@Override
	public ObjNoteDto fromRecord(ObjNoteVRecord note) {
		if (note == null) {
			return null;
		}
		ObjNoteDto.ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder();
		this.fromRecord(dtoBuilder, note);
		// @formatter:off
		return dtoBuilder
			.relatedToId(note.getRelatedToId().toString())
			.noteType(EnumeratedDto.fromEnum(CodeNoteTypeEnum.getNoteType(note.getNoteTypeId())))
			.subject(note.getSubject())
			.content(note.getContent())
			.isPrivate(note.getIsPrivate())
			.build();
		// @formatter:on
	}

}
