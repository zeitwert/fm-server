package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.dddrive.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.service.api.ObjDocumentCache;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;

@Component("objUserDtoAdapter")
@DependsOn("kernelBootstrap")
public class ObjUserDtoAdapter extends ObjDtoAdapterBase<ObjUserFM, ObjUserVRecord, ObjUserDto> {

	private static EnumeratedDto AGGREGATE_TYPE;

	private ObjDocumentCache documentCache = null;
	private ObjDocumentDtoAdapter documentDtoAdapter = null;

	protected ObjUserDtoAdapter() {
		AGGREGATE_TYPE = EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType("obj_user"));
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

	public OffsetDateTime getLastTouch(Integer id) {
		return this.getUserCache().getLastTouch(id);
	}

	@Override
	public void toAggregate(ObjUserDto dto, ObjUserFM obj) {
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
	public ObjUserDto fromAggregate(ObjUserFM obj) {
		if (obj == null) {
			return null;
		}
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder();
		this.fromAggregate(dtoBuilder, obj);
		return dtoBuilder
				.email(obj.getEmail())
				.name(obj.getName())
				.description(obj.getDescription())
				.role(EnumeratedDto.fromEnum(obj.getRole()))
				.tenants(obj.getTenantSet().stream().map(t -> this.getTenantEnumerated(t.getId())).toList())
				.needPasswordChange(obj.getNeedPasswordChange())
				.avatarId(obj.getAvatarImageId())
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
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder();
		this.fromRecord(dtoBuilder, obj);
		return dtoBuilder
				.email(obj.getEmail())
				.name(obj.getName())
				.description(obj.getDescription())
				.role(EnumeratedDto.fromEnum(CodeUserRoleEnum.getUserRole(obj.getRoleList())))
				.avatarId(obj.getAvatarImgId())
				.build();
	}

}
