
package io.zeitwert.fm.obj.adapter.api.jsonapi.base;

import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoBridge;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.fm.obj.model.FMObj;

import org.jooq.TableRecord;

public abstract class FMObjDtoBridge<O extends FMObj, V extends TableRecord<?>, D extends FMObjDtoBase<O>>
		extends ObjDtoBridge<O, V, D> {

	@Override
	public void toAggregate(D dto, O obj) {
		super.toAggregate(dto, obj);
	}

	@Override
	protected void fromAggregate(FMObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, O obj, SessionInfo sessionInfo) {
		super.fromAggregate(dtoBuilder, obj, sessionInfo);
	}

	@Override
	protected void fromRecord(FMObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> obj,
			SessionInfo sessionInfo) {
		super.fromRecord(dtoBuilder, obj, sessionInfo);
	}

}
