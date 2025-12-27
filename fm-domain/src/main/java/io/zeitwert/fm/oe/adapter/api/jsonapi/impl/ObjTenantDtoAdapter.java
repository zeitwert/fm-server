package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.model.ObjTenant;
import io.zeitwert.fm.oe.model.enums.CodeTenantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("objTenantDtoAdapter")
@DependsOn("oeConfig")
public class ObjTenantDtoAdapter extends ObjDtoAdapterBase<ObjTenant, ObjTenantDto> {

	private static EnumeratedDto AGGREGATE_TYPE;

	private ObjDocumentRepository documentRepository = null;
	private ObjDocumentDtoAdapter documentDtoAdapter = null;

	protected ObjTenantDtoAdapter() {
		AGGREGATE_TYPE = EnumeratedDto.of(CodeAggregateTypeEnum.getAggregateType("obj_tenant"));
	}

	@Autowired
	void setDocumentRepository(ObjDocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	@Autowired
	void setDocumentDtoAdapter(ObjDocumentDtoAdapter documentDtoAdapter) {
		this.documentDtoAdapter = documentDtoAdapter;
	}

	public ObjDocumentDto getDocumentDto(Integer id) {
		return id != null ? this.documentDtoAdapter.fromAggregate(this.documentRepository.get(id)) : null;
	}

	@Override
	public void toAggregate(ObjTenantDto dto, ObjTenant obj) {
		super.toAggregate(dto, obj);
		obj.setTenantType(dto.getTenantType() == null ? null : CodeTenantType.getTenantType(dto.getTenantType().getId()));
		obj.setName(dto.getName());
		obj.setDescription(dto.getDescription());
		obj.setInflationRate(dto.getInflationRate());
		obj.setDiscountRate(dto.getDiscountRate());
	}

	@Override
	public ObjTenantDto fromAggregate(ObjTenant obj) {
		if (obj == null) {
			return null;
		}
		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder();
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.tenantType(EnumeratedDto.of(obj.getTenantType()))
				.name(obj.getName())
				.description(obj.getDescription())
				.inflationRate(obj.getInflationRate())
				.discountRate(obj.getDiscountRate())
				.logoId((Integer) obj.getLogoImageId())
				.build();
	}

	public EnumeratedDto asEnumerated(ObjTenant obj) {
		if (obj == null) {
			return null;
		}
		return EnumeratedDto.of("" + obj.getId(), obj.getCaption());
//		return EnumeratedDto.builder()
//				.id("" + obj.getId())
//				.itemType(AGGREGATE_TYPE)
//				.name(obj.getCaption())
//				.build();
	}

//	@Override
//	public ObjTenantDto fromRecord(ObjTenantVRecord obj) {
//		if (obj == null) {
//			return null;
//		}
//		ObjTenantDto.ObjTenantDtoBuilder<?, ?> dtoBuilder = ObjTenantDto.builder();
//		this.fromRecord(dtoBuilder, obj);
//		return dtoBuilder
//				.tenantType(EnumeratedDto.of(CodeTenantTypeEnum.getTenantType(obj.getTenantTypeId())))
//				.name(obj.getName())
//				.description(obj.getDescription())
//				.inflationRate(obj.getInflationRate())
//				.discountRate(obj.getDiscountRate())
//				.logoId(obj.getLogoImgId())
//				.build();
//	}

}
