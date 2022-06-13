package io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiResource;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
@JsonApiResource(type = "note", resourcePath = "collaboration/notes")
public class ObjNoteDto extends ObjDtoBase<ObjNote> {

	private Integer relatedToId;
	private EnumeratedDto noteType;
	private String subject;
	private String content;
	private Boolean isPrivate;

	@Override
	public void toObj(ObjNote note) {
		super.toObj(note);
		note.setRelatedToId(this.relatedToId);
		note.setNoteType(this.noteType == null ? null : CodeNoteTypeEnum.getNoteType(this.noteType.getId()));
		note.setSubject(this.subject);
		note.setContent(this.content);
		note.setIsPrivate(this.isPrivate);
	}

	public static ObjNoteDto fromObj(ObjNote note, SessionInfo sessionInfo) {
		if (note == null) {
			return null;
		}
		ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder().original(note);
		ObjDtoBase.fromObj(dtoBuilder, note, sessionInfo);
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

	public static ObjNoteDto fromRecord(ObjNoteVRecord note, SessionInfo sessionInfo) {
		if (note == null) {
			return null;
		}
		ObjNoteDtoBuilder<?, ?> dtoBuilder = ObjNoteDto.builder().original(null);
		ObjDtoBase.fromRecord(dtoBuilder, note, sessionInfo);
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
