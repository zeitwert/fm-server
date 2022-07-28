
package io.zeitwert.ddd.obj.adapter.api.jsonapi.base;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoAdapter;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjMetaDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.session.model.SessionInfo;

import org.jooq.TableRecord;

public abstract class ObjDtoAdapter<O extends Obj, V extends TableRecord<?>, D extends ObjDtoBase<O>>
		extends AggregateDtoAdapter<O, V, D> {

	@Override
	public void toAggregate(D dto, O obj) {
		if (dto.getOwner() != null) {
			obj.setOwner(getUserRepository().get(dto.getOwner().getId()));
		}
	}

	protected void fromAggregate(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, O obj, SessionInfo sessionInfo) {
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.meta(ObjMetaDto.fromObj(obj, sessionInfo))
			.id(obj.getId())
			.caption(obj.getCaption())
			.owner(ObjUserDtoAdapter.getInstance().fromAggregate(obj.getOwner(), sessionInfo));
		// @formatter:on
	}

	protected void fromRecord(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> obj,
			SessionInfo sessionInfo) {
		ObjUser owner = getUserRepository().get(obj.get(ObjFields.OWNER_ID));
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.meta(ObjMetaDto.fromRecord(obj, sessionInfo))
			.id(obj.get(ObjFields.ID))
			.caption(obj.get(ObjFields.CAPTION))
			.owner(ObjUserDtoAdapter.getInstance().fromAggregate(owner, sessionInfo));
		// @formatter:on
	}

}
