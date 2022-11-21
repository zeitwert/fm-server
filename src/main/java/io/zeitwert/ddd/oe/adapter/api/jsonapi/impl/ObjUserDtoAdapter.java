
package io.zeitwert.ddd.oe.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoAdapter;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;

public final class ObjUserDtoAdapter extends ObjDtoAdapter<ObjUser, ObjUserVRecord, ObjUserDto> {

	private static EnumeratedDto AGGREGATE_TYPE;
	private static ObjUserDtoAdapter INSTANCE;

	private ObjUserDtoAdapter() {
	}

	public static final ObjUserDtoAdapter getInstance() {
		if (INSTANCE == null) {
			AGGREGATE_TYPE = EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType("obj_user"));
			INSTANCE = new ObjUserDtoAdapter();
		}
		return INSTANCE;
	}

	@Override
	public void toAggregate(ObjUserDto dto, ObjUser obj) {
		super.toAggregate(dto, obj);
		// if (dto.getTenant() != null) {
		// obj.setTenant(dto.getTenant());
		// }
		obj.setEmail(dto.getEmail());
		if (dto.getPassword() != null) {
			obj.setPassword("{noop}" + dto.getPassword());
		}
		obj.setRole(CodeUserRoleEnum.getUserRole(dto.getRole()));
		obj.setName(dto.getName());
		obj.setDescription(dto.getDescription());
		// obj.setPicture(dto.getPicture());
	}

	@Override
	public ObjUserDto fromAggregate(ObjUser obj) {
		if (obj == null) {
			return null;
		}
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.tenant(ObjTenantDtoAdapter.getInstance().asEnumerated(obj.getTenant()))
				.email(obj.getEmail())
				.role(obj.getRole().getId())
				.name(obj.getName())
				.description(obj.getDescription())
				.build();
	}

	public EnumeratedDto asEnumerated(ObjUser obj) {
		if (obj == null) {
			return null;
		}
		return EnumeratedDto.builder()
				.id("" + obj.getId())
				.itemType(AGGREGATE_TYPE)
				.name(obj.getCaption())
				.build();
	}

	@Override
	public ObjUserDto fromRecord(ObjUserVRecord obj) {
		if (obj == null) {
			return null;
		}
		EnumeratedDto tenant = getTenantEnumerated(obj.getTenantId());
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder().original(null);
		this.fromRecord(dtoBuilder, obj);
		return dtoBuilder
				.tenant(tenant)
				.email(obj.getEmail())
				.role(obj.getRoleList())
				.name(obj.getName())
				.description(obj.getDescription())
				.build();
	}

}
