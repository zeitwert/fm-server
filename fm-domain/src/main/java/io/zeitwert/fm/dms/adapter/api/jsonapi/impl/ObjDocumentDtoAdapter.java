package io.zeitwert.fm.dms.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
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
			obj.name = dto.getName();
			obj.contentKind = dto.getContentKind() == null ? null
					: CodeContentKind.getContentKind(
					dto.getContentKind().getId());
			obj.documentKind = dto.getDocumentKind() == null ? null : CodeDocumentKind.getDocumentKind(dto.getDocumentKind().getId());
			obj.documentCategory = dto.getDocumentCategory() == null ? null
					: CodeDocumentCategory.getDocumentCategory(
					dto.getDocumentCategory().getId());
		} finally {
			obj.getMeta().enableCalc();
			obj.calcAll();
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
			.name(obj.name)
			.contentKind(EnumeratedDto.of(obj.contentKind))
			.supportedContentTypes(obj.contentKind.getExtensions().stream().reduce("", (a, b) -> a.length() > 0 ? a + "," + b : b))
			.documentKind(EnumeratedDto.of(obj.documentKind))
			.documentCategory(EnumeratedDto.of(obj.documentCategory))
			.contentType(EnumeratedDto.of(obj.contentType))
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
