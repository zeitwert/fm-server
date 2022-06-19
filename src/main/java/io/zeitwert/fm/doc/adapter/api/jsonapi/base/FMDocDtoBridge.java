
package io.zeitwert.fm.doc.adapter.api.jsonapi.base;

import io.zeitwert.ddd.doc.adapter.api.jsonapi.base.DocDtoBridge;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.FMDocDtoBase;
import io.zeitwert.fm.doc.model.FMDoc;

import org.jooq.TableRecord;

public abstract class FMDocDtoBridge<A extends FMDoc, V extends TableRecord<?>, D extends FMDocDtoBase<A>>
		extends DocDtoBridge<A, V, D> {

	@Override
	public void toAggregate(D dto, A obj) {
		super.toAggregate(dto, obj);
	}

	@Override
	protected void fromAggregate(FMDocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, A doc, SessionInfo sessionInfo) {
		super.fromAggregate(dtoBuilder, doc, sessionInfo);
	}

	@Override
	protected void fromRecord(FMDocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> doc,
			SessionInfo sessionInfo) {
		super.fromRecord(dtoBuilder, doc, sessionInfo);
	}

}
