package io.zeitwert.fm.dms.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.enums.CodeContentKind;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import org.springframework.stereotype.Component;

@Component("objDocumentDtoAdapter")
public class ObjDocumentDtoAdapter extends ObjDtoAdapterBase<ObjDocument, ObjDocumentDto> {

	@Override
	public void toAggregate(ObjDocumentDto dto, ObjDocument obj) {
		try {
			obj.getMeta().disableCalc();
			super.toAggregate(dto, obj);
			obj.setName(dto.getName());
			obj.setContentKind(dto.getContentKind() == null ? null
					: CodeContentKind.getContentKind(
					dto.getContentKind().getId()));
			obj.setDocumentKind(dto.getDocumentKind() == null ? null : CodeDocumentKind.getDocumentKind(dto.getDocumentKind().getId()));
			obj.setDocumentCategory(dto.getDocumentCategory() == null ? null
					: CodeDocumentCategory.getDocumentCategory(
					dto.getDocumentCategory().getId()));
		} finally {
			obj.getMeta().enableCalc();
			obj.getMeta().calcAll();
		}
	}

	@Override
	public ObjDocumentDto fromAggregate(ObjDocument obj) {
		if (obj == null) {
			return null;
		}
		ObjDocumentDto.ObjDocumentDtoBuilder<?, ?> dtoBuilder = ObjDocumentDto.builder();
		this.fromAggregate(dtoBuilder, obj);
		// @formatter:off
		return dtoBuilder
			.name(obj.getName())
			.contentKind(EnumeratedDto.of(obj.getContentKind()))
			.supportedContentTypes(obj.getContentKind().getExtensions().stream().reduce("", (a, b) -> a.length() > 0 ? a + "," + b : b))
			.documentKind(EnumeratedDto.of(obj.getDocumentKind()))
			.documentCategory(EnumeratedDto.of(obj.getDocumentCategory()))
			.contentType(EnumeratedDto.of(obj.getContentType()))
			.build();
		// @formatter:on
	}

//	@Override
//	public ObjDocumentDto fromRecord(ObjDocumentVRecord obj) {
//		if (obj == null) {
//			return null;
//		}
//		ObjDocumentDto.ObjDocumentDtoBuilder<?, ?> dtoBuilder = ObjDocumentDto.builder();
//		this.fromRecord(dtoBuilder, obj);
//		// @formatter:off
//		return dtoBuilder
//			.name(obj.getName())
//			.contentKind(EnumeratedDto.of(CodeContentKindEnum.getContentKind(obj.getContentKindId())))
//			.documentKind(EnumeratedDto.of(CodeDocumentKindEnum.getDocumentKind(obj.getDocumentKindId())))
//			.documentCategory(EnumeratedDto.of(CodeDocumentCategoryEnum.getDocumentCategory(obj.getDocumentCategoryId())))
//			.build();
//		// @formatter:on
//	}

}
