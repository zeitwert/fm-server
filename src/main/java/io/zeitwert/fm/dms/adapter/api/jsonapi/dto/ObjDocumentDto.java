package io.zeitwert.fm.dms.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiResource;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.session.model.SessionInfo;
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
@JsonApiResource(type = "document", resourcePath = "document/documents", deletable = false)
public class ObjDocumentDto extends FMObjDtoBase<ObjDocument> {

	private String name;
	private EnumeratedDto contentKind;
	private String supportedContentTypes;
	private EnumeratedDto documentKind;
	private EnumeratedDto documentCategory;
	private EnumeratedDto contentType;

	@Override
	public void toObj(ObjDocument obj) {
		super.toObj(obj);
		obj.setName(name);
		obj.setContentKind(contentKind == null ? null : CodeContentKindEnum.getContentKind(contentKind.getId()));
		obj.setDocumentKind(documentKind == null ? null : CodeDocumentKindEnum.getDocumentKind(documentKind.getId()));
		obj.setDocumentCategory(
				documentCategory == null ? null : CodeDocumentCategoryEnum.getDocumentCategory(documentCategory.getId()));
	}

	public static ObjDocumentDto fromObj(ObjDocument obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjDocumentDtoBuilder<?, ?> dtoBuilder = ObjDocumentDto.builder().original(obj);
		FMObjDtoBase.fromObj(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.name(obj.getName())
			.contentKind(EnumeratedDto.fromEnum(obj.getContentKind()))
			.supportedContentTypes(obj.getContentKind().getExtensionList().stream().reduce("", (a, b) -> a.length() > 0 ? a + "," + b : b))
			.documentKind(EnumeratedDto.fromEnum(obj.getDocumentKind()))
			.documentCategory(EnumeratedDto.fromEnum(obj.getDocumentCategory()))
			.contentType(EnumeratedDto.fromEnum(obj.getContentType()))
			.build();
		// @formatter:on
	}

	public static ObjDocumentDto fromRecord(ObjDocumentVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjDocumentDtoBuilder<?, ?> dtoBuilder = ObjDocumentDto.builder().original(null);
		FMObjDtoBase.fromRecord(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		return dtoBuilder
			.name(obj.getName())
			.contentKind(EnumeratedDto.fromEnum(CodeContentKindEnum.getContentKind(obj.getContentKindId())))
			.documentKind(EnumeratedDto.fromEnum(CodeDocumentKindEnum.getDocumentKind(obj.getDocumentKindId())))
			.documentCategory(EnumeratedDto.fromEnum(CodeDocumentCategoryEnum.getDocumentCategory(obj.getDocumentCategoryId())))
			.build();
		// @formatter:on
	}

}
