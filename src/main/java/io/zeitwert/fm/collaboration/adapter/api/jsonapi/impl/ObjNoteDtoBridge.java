
package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoBridge;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;

public final class ObjNoteDtoBridge extends ObjDtoBridge<ObjNote, ObjNoteVRecord, ObjNoteDto> {

	private static ObjNoteDtoBridge instance;

	private ObjNoteDtoBridge() {
	}

	public static final ObjNoteDtoBridge getInstance() {
		if (instance == null) {
			instance = new ObjNoteDtoBridge();
		}
		return instance;
	}

	@Override
	public void toAggregate(ObjNoteDto dto, ObjNote note) {
		super.toAggregate(dto, note);
		note.setRelatedToId(dto.getRelatedToId());
		note.setNoteType(dto.getNoteType() == null ? null : CodeNoteTypeEnum.getNoteType(dto.getNoteType().getId()));
		note.setSubject(dto.getSubject());
		note.setContent(dto.getContent());
		note.setIsPrivate(dto.getIsPrivate());
	}

	@Override
	public ObjNoteDto fromAggregate(ObjNote note, SessionInfo sessionInfo) {
		if (note == null) {
			return null;
		}
		ObjNoteDto.ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder().original(note);
		this.fromAggregate(dtoBuilder, note, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.relatedToId(note.getRelatedToId())
			.noteType(EnumeratedDto.fromEnum(note.getNoteType()))
			.subject(note.getSubject())
			.content(note.getContent())
			.isPrivate(note.getIsPrivate())
			.build();
		// @formatter:on
	}

	@Override
	public ObjNoteDto fromRecord(ObjNoteVRecord note, SessionInfo sessionInfo) {
		if (note == null) {
			return null;
		}
		ObjNoteDto.ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder().original(null);
		this.fromRecord(dtoBuilder, note, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.relatedToId(note.getRelatedToId())
			.noteType(EnumeratedDto.fromEnum(CodeNoteTypeEnum.getNoteType(note.getNoteTypeId())))
			.subject(note.getSubject())
			.content(note.getContent())
			.isPrivate(note.getIsPrivate())
			.build();
		// @formatter:on
	}

}
