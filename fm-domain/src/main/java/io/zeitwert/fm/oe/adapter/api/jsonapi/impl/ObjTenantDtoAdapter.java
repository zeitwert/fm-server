package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.dddrive.oe.model.ObjTenant;
import io.zeitwert.fm.oe.model.enums.CodeTenantTypeEnum;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.service.api.ObjDocumentCache;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

@Component("objTenantDtoAdapter")
public class ObjTenantDtoAdapter extends ObjDtoAdapterBase<ObjTenantFM, ObjTenantVRecord, ObjTenantDto> {

	private static EnumeratedDto AGGREGATE_TYPE;

	private ObjDocumentCache documentCache = null;
	private ObjDocumentDtoAdapter documentDtoAdapter = null;

	protected ObjTenantDtoAdapter() {
		AGGREGATE_TYPE = EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType("obj_tenant"));
	}

	@Autowired
	void setDocumentCache(ObjDocumentCache documentCache) {
		this.documentCache = documentCache;
	}

	@Autowired
	void setDocumentDtoAdapter(ObjDocumentDtoAdapter documentDtoAdapter) {
		this.documentDtoAdapter = documentDtoAdapter;
	}

	public ObjDocumentDto getDocumentDto(Integer id) {
		return id != null ? this.documentDtoAdapter.fromAggregate(this.documentCache.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjTenantDto dto, ObjTenantFM obj) {
		super.toAggregate(dto, obj);
		obj.setTenantType(
				dto.getTenantType() == null ? null : CodeTenantTypeEnum.getTenantType(dto.getTenantType().getId()));
		obj.setName(dto.getName());
		obj.setDescription(dto.getDescription());
		obj.setInflationRate(dto.getInflationRate());
		obj.setDiscountRate(dto.getDiscountRate());
	}

	@Override
	public ObjTenantDto fromAggregate(ObjTenantFM obj) {
		if (obj == null) {
			return null;
		}
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder();
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.tenantType(EnumeratedDto.fromEnum(obj.getTenantType()))
				.name(obj.getName())
				.description(obj.getDescription())
				.inflationRate(obj.getInflationRate())
				.discountRate(obj.getDiscountRate())
				.logoId(obj.getLogoImageId())
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
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder();
		this.fromRecord(dtoBuilder, obj);
		return dtoBuilder
				.tenantType(EnumeratedDto.fromEnum(CodeTenantTypeEnum.getTenantType(obj.getTenantTypeId())))
				.name(obj.getName())
				.description(obj.getDescription())
				.inflationRate(obj.getInflationRate())
				.discountRate(obj.getDiscountRate())
				.logoId(obj.getLogoImgId())
				.build();
	}

}
