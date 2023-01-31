
package io.zeitwert.ddd.obj.adapter.api.jsonapi.base;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateDtoAdapterBase;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjMetaDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;

import org.jooq.TableRecord;

public abstract class ObjDtoAdapterBase<O extends Obj, V extends TableRecord<?>, D extends ObjDtoBase<O>>
		extends AggregateDtoAdapterBase<O, V, D> {

	@Override
	public void toAggregate(D dto, O obj) {
		if (dto.getOwner() != null) {
			obj.setOwner(getUser(Integer.parseInt(dto.getOwner().getId())));
		}
	}

	protected void fromAggregate(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, O obj) {
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		dtoBuilder
			.tenant(tenantDtoAdapter.asEnumerated(obj.getTenant()))
			.meta(ObjMetaDto.fromObj(obj))
			.id(obj.getId())
			.caption(obj.getCaption())
			.owner(userDtoAdapter.asEnumerated(obj.getOwner()));
		// @formatter:on
	}

	protected void fromRecord(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> obj) {
		EnumeratedDto tenant = getTenantEnumerated(obj.get(ObjFields.TENANT_ID));
		EnumeratedDto owner = getUserEnumerated(obj.get(ObjFields.OWNER_ID));
		// @formatter:off
		dtoBuilder
			.tenant(tenant)
			.meta(ObjMetaDto.fromRecord(obj))
			.id(obj.get(ObjFields.ID))
			.caption(obj.get(ObjFields.CAPTION))
			.owner(owner);
		// @formatter:on
	}

}
