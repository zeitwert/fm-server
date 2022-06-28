
package io.zeitwert.ddd.oe.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoBridge;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;

public final class ObjUserDtoBridge extends ObjDtoBridge<ObjUser, ObjUserVRecord, ObjUserDto> {

	private static ObjUserDtoBridge instance;

	private ObjUserDtoBridge() {
	}

	public static final ObjUserDtoBridge getInstance() {
		if (instance == null) {
			instance = new ObjUserDtoBridge();
		}
		return instance;
	}

	@Override
	public void toAggregate(ObjUserDto dto, ObjUser obj) {
		super.toAggregate(dto, obj);
		// @formatter:off
		obj.setName(dto.getName());
		// obj.setEmail(dto.getEmail());
		// obj.setPicture(dto.getPicture());
		// @formatter:on
	}

	@Override
	public ObjUserDto fromAggregate(ObjUser obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		// @formatter:off
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder().original(obj);
		// @formatter:off
		dtoBuilder // we do not user super.fromAggregate, since meta would cause infinite loop (and we dont need it)
			.sessionInfo(sessionInfo)
			.id(obj.getId())
			.caption(obj.getCaption())
			//.owner(ObjUserDtoBridge.getInstance().fromAggregate(obj.getOwner(), sessionInfo))
			// user stuff
			.name(obj.getName())
			.email(obj.getEmail())
			.roles(obj.getRoleList().stream().map(r -> r.getId()).toList())
			.picture(obj.getPicture());
		// @formatter:on
		return dtoBuilder.build();
	}

	@Override
	public ObjUserDto fromRecord(ObjUserVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		// @formatter:off
		//ObjUser owner = getUserRepository().get(obj.getOwnerId());
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder().original(null);
		dtoBuilder
			.sessionInfo(sessionInfo)
			.id(obj.getId())
			.caption(obj.getCaption())
			//.owner(ObjUserDtoBridge.getInstance().fromAggregate(owner, sessionInfo))
			// user stuff
			.name(obj.getName())
			.email(obj.getEmail())
			.picture(obj.getPicture());
		// @formatter:on
		return dtoBuilder.build();
	}

}
