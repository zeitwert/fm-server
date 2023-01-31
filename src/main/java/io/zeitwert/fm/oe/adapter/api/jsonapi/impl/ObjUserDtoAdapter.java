
package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;

public final class ObjUserDtoAdapter extends ObjDtoAdapterBase<ObjUser, TableRecord<?>, ObjUserDto> {

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
		if (dto.getId() != null && dto.getPassword() != null) {
			obj.setPassword(dto.getPassword());
			obj.setNeedPasswordChange(dto.getNeedPasswordChange());
		} else {
			obj.setEmail(dto.getEmail());
			if (dto.getId() == null) {
				obj.setPassword(dto.getPassword());
				obj.setNeedPasswordChange(dto.getNeedPasswordChange());
			}
			obj.setName(dto.getName());
			obj.setDescription(dto.getDescription());
			obj.setRole(CodeUserRoleEnum.getUserRole(dto.getRole().getId()));
			obj.clearTenantSet();
			for (EnumeratedDto tenant : dto.getTenants()) {
				obj.addTenant(this.getTenant(Integer.parseInt(tenant.getId())));
			}
		}
	}

	@Override
	public ObjUserDto fromAggregate(ObjUser obj) {
		if (obj == null) {
			return null;
		}
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.email(obj.getEmail())
				.name(obj.getName())
				.description(obj.getDescription())
				.role(EnumeratedDto.fromEnum(obj.getRole()))
				.tenants(obj.getTenantSet().stream().map(t -> this.getTenantEnumerated(t.getId())).toList())
				.needPasswordChange(obj.getNeedPasswordChange())
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
	public ObjUserDto fromRecord(TableRecord<?> tr) {
		if (tr == null) {
			return null;
		}
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder().original(null);
		this.fromRecord(dtoBuilder, tr);
		ObjUserVRecord obj = (ObjUserVRecord) tr;
		return dtoBuilder
				.email(obj.getEmail())
				.name(obj.getName())
				.description(obj.getDescription())
				.role(EnumeratedDto.fromEnum(CodeUserRoleEnum.getUserRole(obj.getRoleList())))
				.build();
	}

}
