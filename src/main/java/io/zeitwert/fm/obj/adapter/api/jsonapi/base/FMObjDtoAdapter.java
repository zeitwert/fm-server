
package io.zeitwert.fm.obj.adapter.api.jsonapi.base;

import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoAdapter;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.fm.obj.model.FMObj;

import org.jooq.TableRecord;

public abstract class FMObjDtoAdapter<O extends FMObj, V extends TableRecord<?>, D extends FMObjDtoBase<O>>
		extends ObjDtoAdapter<O, V, D> {

	@Override
	public void toAggregate(D dto, O obj, RequestContext requestCtx) {
		super.toAggregate(dto, obj, requestCtx);
	}

	@Override
	protected void fromAggregate(FMObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, O obj, RequestContext requestCtx) {
		super.fromAggregate(dtoBuilder, obj, requestCtx);
	}

	@Override
	protected void fromRecord(FMObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> obj,
			RequestContext requestCtx) {
		super.fromRecord(dtoBuilder, obj, requestCtx);
	}

}
