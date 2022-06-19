
package io.zeitwert.ddd.oe.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoBridge;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;

public final class ObjTenantDtoBridge extends ObjDtoBridge<ObjTenant, ObjTenantVRecord, ObjTenantDto> {

	private static ObjTenantDtoBridge instance;

	private ObjTenantDtoBridge() {
	}

	public static final ObjTenantDtoBridge getInstance() {
		if (instance == null) {
			instance = new ObjTenantDtoBridge();
		}
		return instance;
	}

	@Override
	public void toAggregate(ObjTenantDto dto, ObjTenant obj) {
		super.toAggregate(dto, obj);
		obj.setName(dto.getName());
		// obj.setExtlKey(dto.getExtlKey());
	}

	@Override
	public ObjTenantDto fromAggregate(ObjTenant obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		// @formatter:off
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder().original(obj);
		dtoBuilder // we do not user super.fromAggregate, since meta would cause infinite loop (and we dont need it)
			.sessionInfo(sessionInfo)
			.id(obj.getId())
			.caption(obj.getCaption())
			.owner(ObjUserDtoBridge.getInstance().fromAggregate(obj.getOwner(), sessionInfo))
			// tenant stuff
			.name(obj.getName())
			.extlKey(obj.getExtlKey());
		// @formatter:on
		return dtoBuilder.build();
	}

	@Override
	public ObjTenantDto fromRecord(ObjTenantVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		// @formatter:off
		ObjUser owner = getUserRepository().get(obj.getOwnerId());
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder().original(null);
		dtoBuilder
			.sessionInfo(sessionInfo)
			.id(obj.getId())
			.caption(obj.getCaption())
			.owner(ObjUserDtoBridge.getInstance().fromAggregate(owner, sessionInfo))
			// tenant stuff
			.name(obj.getName())
			.extlKey(obj.getExtlKey());
		// @formatter:on
		return dtoBuilder.build();
	}

}
