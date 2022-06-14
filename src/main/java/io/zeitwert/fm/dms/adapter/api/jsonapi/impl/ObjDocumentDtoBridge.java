
package io.zeitwert.fm.dms.adapter.api.jsonapi.impl;

import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.FMObjDtoBridge;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.session.model.SessionInfo;

public final class ObjDocumentDtoBridge extends FMObjDtoBridge<ObjDocument, ObjDocumentVRecord, ObjDocumentDto> {

	private static ObjDocumentDtoBridge instance;

	private ObjDocumentDtoBridge() {
	}

	public static final ObjDocumentDtoBridge getInstance() {
		if (instance == null) {
			instance = new ObjDocumentDtoBridge();
		}
		return instance;
	}

	@Override
	public void toAggregate(ObjDocumentDto dto, ObjDocument obj) {
		super.toAggregate(dto, obj);
		obj.setName(dto.getName());
		obj.setContentKind(dto.getContentKind() == null ? null
				: CodeContentKindEnum.getContentKind(
						dto.getContentKind().getId()));
		obj.setDocumentKind(
				dto.getDocumentKind() == null ? null : CodeDocumentKindEnum.getDocumentKind(dto.getDocumentKind().getId()));
		obj.setDocumentCategory(
				dto.getDocumentCategory() == null ? null
						: CodeDocumentCategoryEnum.getDocumentCategory(
								dto.getDocumentCategory().getId()));
	}

	@Override
	public ObjDocumentDto fromAggregate(ObjDocument obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjDocumentDto.ObjDocumentDtoBuilder<?, ?> dtoBuilder = ObjDocumentDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj, sessionInfo);
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

	@Override
	public ObjDocumentDto fromRecord(ObjDocumentVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjDocumentDto.ObjDocumentDtoBuilder<?, ?> dtoBuilder = ObjDocumentDto.builder().original(null);
		this.fromRecord(dtoBuilder, obj, sessionInfo);
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
