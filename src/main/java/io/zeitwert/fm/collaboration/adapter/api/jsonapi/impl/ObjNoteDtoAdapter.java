
package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoAdapter;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;

public final class ObjNoteDtoAdapter extends ObjDtoAdapter<ObjNote, ObjNoteVRecord, ObjNoteDto> {

	private static ObjNoteDtoAdapter instance;

	private ObjNoteDtoAdapter() {
	}

	public static final ObjNoteDtoAdapter getInstance() {
		if (instance == null) {
			instance = new ObjNoteDtoAdapter();
		}
		return instance;
	}

	@Override
	public void toAggregate(ObjNoteDto dto, ObjNote note, RequestContext requestCtx) {
		super.toAggregate(dto, note, requestCtx);
		note.setRelatedToId(Integer.parseInt(dto.getRelatedToId()));
		note.setNoteType(dto.getNoteType() == null ? null : CodeNoteTypeEnum.getNoteType(dto.getNoteType().getId()));
		note.setSubject(dto.getSubject());
		note.setContent(dto.getContent());
		note.setIsPrivate(dto.getIsPrivate());
	}

	@Override
	public ObjNoteDto fromAggregate(ObjNote note, RequestContext requestCtx) {
		if (note == null) {
			return null;
		}
		ObjNoteDto.ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder().original(note);
		this.fromAggregate(dtoBuilder, note, requestCtx);
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
	public ObjNoteDto fromRecord(ObjNoteVRecord note, RequestContext requestCtx) {
		if (note == null) {
			return null;
		}
		ObjNoteDto.ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder().original(null);
		this.fromRecord(dtoBuilder, note, requestCtx);
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
