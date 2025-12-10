package io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto;

// TODO-MIGRATION: REST-API - remove after Phase 3 (REST API migration)
// This JSON:API DTO uses OLD dddrive base classes. Will be replaced with REST DTOs in Phase 3.

/*
import io.crnk.core.resource.annotations.JsonApiResource;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
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

	private String relatedToId;
	private EnumeratedDto noteType;
	private String subject;
	private String content;
	private Boolean isPrivate;

}
*/
