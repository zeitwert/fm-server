
package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.enums.CodeTenantTypeEnum;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

public final class ObjTenantDtoAdapter extends ObjDtoAdapterBase<ObjTenant, TableRecord<?>, ObjTenantDto> {

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
	public void toAggregate(ObjTenantDto dto, ObjTenant obj) {
		super.toAggregate(dto, obj);
		obj.setTenantType(
				dto.getTenantType() == null ? null : CodeTenantTypeEnum.getTenantType(dto.getTenantType().getId()));
		obj.setName(dto.getName());
		obj.setDescription(dto.getDescription());
		obj.setInflationRate(dto.getInflationRate());
	}

	@Override
	public ObjTenantDto fromAggregate(ObjTenant obj) {
		if (obj == null) {
			return null;
		}
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder().original(obj);
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.tenantType(EnumeratedDto.fromEnum(obj.getTenantType()))
				.name(obj.getName())
				.description(obj.getDescription())
				.inflationRate(obj.getInflationRate())
				.build();
	}

	public EnumeratedDto asEnumerated(ObjTenant obj) {
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
	public ObjTenantDto fromRecord(TableRecord<?> tr) {
		if (tr == null) {
			return null;
		}
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder().original(null);
		this.fromRecord(dtoBuilder, tr);
		ObjTenantVRecord obj = (ObjTenantVRecord) tr;
		return dtoBuilder
				.tenantType(EnumeratedDto.fromEnum(CodeTenantTypeEnum.getTenantType(obj.getTenantTypeId())))
				.name(obj.getName())
				.description(obj.getDescription())
				.inflationRate(obj.getInflationRate())
				.build();
	}

}
