package io.zeitwert.ddd.doc.adapter.api.jsonapi.base;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoBridge;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocDtoBase;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocMetaDto;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocFields;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.session.model.SessionInfo;

import org.jooq.TableRecord;

public abstract class DocDtoBridge<A extends Doc, V extends TableRecord<?>, D extends DocDtoBase<A>>
		extends AggregateDtoBridge<A, V, D> {

	@Override
	public void toAggregate(D dto, A doc) {
		doc.setOwner(dto.getOwner() != null ? getUserRepository().get(dto.getOwner().getId()) : null);
	}

	protected void fromAggregate(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, A doc, SessionInfo sessionInfo) {
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.meta(DocMetaDto.fromDoc(doc, sessionInfo))
			.id(doc.getId())
			.caption(doc.getCaption())
			.owner(ObjUserDto.fromObj(doc.getOwner()));
		// @formatter:on
	}

	protected void fromRecord(DocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> doc,
			SessionInfo sessionInfo) {
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.meta(DocMetaDto.fromRecord(doc, sessionInfo))
			.id(doc.get(DocFields.ID))
			.caption(doc.get(DocFields.CAPTION));
			//.owner(ObjUserDto.fromObj(doc.getOwner()))
		// @formatter:on
	}

}
