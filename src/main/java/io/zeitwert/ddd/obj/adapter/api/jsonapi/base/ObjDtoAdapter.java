
package io.zeitwert.ddd.obj.adapter.api.jsonapi.base;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoAdapter;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjMetaDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.session.model.SessionInfo;

import org.jooq.TableRecord;

public abstract class ObjDtoAdapter<O extends Obj, V extends TableRecord<?>, D extends ObjDtoBase<O>>
		extends AggregateDtoAdapter<O, V, D> {

	@Override
	public void toAggregate(D dto, O obj, SessionInfo sessionInfo) {
		if (dto.getOwner() != null) {
			obj.setOwner(getUserRepository().get(sessionInfo, Integer.parseInt(dto.getOwner().getId())));
		}
	}

	protected void fromAggregate(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, O obj, SessionInfo sessionInfo) {
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.tenant(tenantDtoAdapter.asEnumerated(obj.getTenant(), sessionInfo))
			.meta(ObjMetaDto.fromObj(obj, sessionInfo))
			.id(obj.getId())
			.caption(obj.getCaption())
			.owner(userDtoAdapter.asEnumerated(obj.getOwner(), sessionInfo));
		// @formatter:on
	}

	protected void fromRecord(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> obj,
			SessionInfo sessionInfo) {
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjTenant tenant = getTenantRepository().get(sessionInfo, obj.get(ObjFields.TENANT_ID));
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		ObjUser owner = getUserRepository().get(sessionInfo, obj.get(ObjFields.OWNER_ID));
		// @formatter:off
		dtoBuilder
			.sessionInfo(sessionInfo)
			.tenant(tenantDtoAdapter.asEnumerated(tenant, sessionInfo))
			.meta(ObjMetaDto.fromRecord(obj, sessionInfo))
			.id(obj.get(ObjFields.ID))
			.caption(obj.get(ObjFields.CAPTION))
			.owner(userDtoAdapter.asEnumerated(owner, sessionInfo));
		// @formatter:on
	}

}
