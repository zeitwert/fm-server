
package io.zeitwert.fm.doc.adapter.api.jsonapi.base;

import io.zeitwert.ddd.doc.adapter.api.jsonapi.base.DocDtoAdapter;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.doc.adapter.api.jsonapi.dto.FMDocDtoBase;
import io.zeitwert.fm.doc.model.FMDoc;

import org.jooq.TableRecord;

public abstract class FMDocDtoAdapter<A extends FMDoc, V extends TableRecord<?>, D extends FMDocDtoBase<A>>
		extends DocDtoAdapter<A, V, D> {

	@Override
	public void toAggregate(D dto, A obj, RequestContext requestCtx) {
		super.toAggregate(dto, obj, requestCtx);
	}

	@Override
	protected void fromAggregate(FMDocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, A doc, RequestContext requestCtx) {
		super.fromAggregate(dtoBuilder, doc, requestCtx);
	}

	@Override
	protected void fromRecord(FMDocDtoBase.DocDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> doc,
			RequestContext requestCtx) {
		super.fromRecord(dtoBuilder, doc, requestCtx);
	}

}
