
package io.zeitwert.ddd.oe.adapter.api.jsonapi.impl;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoAdapter;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.oe.model.enums.CodeTenantTypeEnum;
import io.zeitwert.ddd.session.model.SessionInfo;

public final class ObjTenantDtoAdapter extends ObjDtoAdapter<ObjTenant, ObjTenantVRecord, ObjTenantDto> {

	private static EnumeratedDto AGGREGATE_TYPE;
	private static ObjTenantDtoAdapter INSTANCE;

	private ObjTenantDtoAdapter() {
	}

	public static final ObjTenantDtoAdapter getInstance() {
		if (INSTANCE == null) {
			AGGREGATE_TYPE = EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType("obj_tenant"));
			INSTANCE = new ObjTenantDtoAdapter();
		}
		return INSTANCE;
	}

	@Override
	public void toAggregate(ObjTenantDto dto, ObjTenant obj, SessionInfo sessionInfo) {
		super.toAggregate(dto, obj, sessionInfo);
		obj.setTenantType(
				dto.getTenantType() == null ? null : CodeTenantTypeEnum.getTenantType(dto.getTenantType().getId()));
		obj.setName(dto.getName());
		obj.setExtlKey(dto.getExtlKey());
		obj.setDescription(dto.getDescription());
	}

	@Override
	public ObjTenantDto fromAggregate(ObjTenant obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj, sessionInfo);
		return dtoBuilder
				.name(obj.getName())
				.description(obj.getDescription())
				.extlKey(obj.getExtlKey())
				.tenantType(EnumeratedDto.fromEnum(obj.getTenantType()))
				.build();
	}

	public EnumeratedDto asEnumerated(ObjTenant obj, SessionInfo sessionInfo) {
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
	public ObjTenantDto fromRecord(ObjTenantVRecord obj, SessionInfo sessionInfo) {
		if (obj == null) {
			return null;
		}
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder().original(null);
		this.fromRecord(dtoBuilder, obj, sessionInfo);
		return dtoBuilder
				.name(obj.getName())
				.description(obj.getDescription())
				.extlKey(obj.getExtlKey())
				.tenantType(EnumeratedDto.fromEnum(CodeTenantTypeEnum.getTenantType(obj.getTenantTypeId())))
				.build();
	}

}
