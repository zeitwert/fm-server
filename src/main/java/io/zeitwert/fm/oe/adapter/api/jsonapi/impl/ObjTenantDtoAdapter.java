
package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.model.enums.CodeTenantTypeEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

@Component("objTenantDtoAdapter")
public class ObjTenantDtoAdapter extends ObjDtoAdapterBase<ObjTenantFM, ObjTenantVRecord, ObjTenantDto> {

	private static EnumeratedDto AGGREGATE_TYPE;

	protected ObjTenantDtoAdapter(AppContext appContext) {
		super(appContext);
		AGGREGATE_TYPE = EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType("obj_tenant"));
	}

	@Override
	public void toAggregate(ObjTenantDto dto, ObjTenantFM obj) {
		super.toAggregate(dto, obj);
		obj.setTenantType(
				dto.getTenantType() == null ? null : CodeTenantTypeEnum.getTenantType(dto.getTenantType().getId()));
		obj.setName(dto.getName());
		obj.setDescription(dto.getDescription());
		obj.setInflationRate(dto.getInflationRate());
	}

	@Override
	public ObjTenantDto fromAggregate(ObjTenantFM obj) {
		if (obj == null) {
			return null;
		}
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder()
				.appContext(this.getAppContext())
				.original(obj);
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
	public ObjTenantDto fromRecord(ObjTenantVRecord obj) {
		if (obj == null) {
			return null;
		}
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder()
				.appContext(this.getAppContext())
				.original(null);
		this.fromRecord(dtoBuilder, obj);
		return dtoBuilder
				.tenantType(EnumeratedDto.fromEnum(CodeTenantTypeEnum.getTenantType(obj.getTenantTypeId())))
				.name(obj.getName())
				.description(obj.getDescription())
				.inflationRate(obj.getInflationRate())
				.build();
	}

}
