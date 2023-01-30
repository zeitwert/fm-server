
package io.zeitwert.fm.obj.adapter.api.jsonapi.base;

import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoAdapter;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.fm.obj.model.FMObj;

import org.jooq.TableRecord;

public abstract class FMObjDtoAdapter<O extends FMObj, V extends TableRecord<V>, D extends FMObjDtoBase<O>>
		extends ObjDtoAdapter<O, V, D> {

	@Override
	public void toAggregate(D dto, O obj) {
		super.toAggregate(dto, obj);
	}

	@Override
	protected void fromAggregate(FMObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, O obj) {
		super.fromAggregate(dtoBuilder, obj);
	}

	@Override
	protected void fromRecord(FMObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> obj) {
		super.fromRecord(dtoBuilder, obj);
	}

}
