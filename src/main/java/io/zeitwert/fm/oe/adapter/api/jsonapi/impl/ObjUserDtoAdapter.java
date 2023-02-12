
package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.dddrive.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;

@Component("objUserDtoAdapter")
@DependsOn("kernelBootstrap")
public class ObjUserDtoAdapter extends ObjDtoAdapterBase<ObjUserFM, ObjUserVRecord, ObjUserDto> {

	private static EnumeratedDto AGGREGATE_TYPE;

	protected ObjUserDtoAdapter(AppContext appContext) {
		super(appContext);
		AGGREGATE_TYPE = EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType("obj_user"));
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
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder()
				.appContext(this.getAppContext())
				.original(obj);
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

	public EnumeratedDto asEnumerated(ObjUserFM obj) {
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
		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder()
				.appContext(this.getAppContext())
				.original(null);
		this.fromRecord(dtoBuilder, obj);
		return dtoBuilder
				.email(obj.getEmail())
				.name(obj.getName())
				.description(obj.getDescription())
				.role(EnumeratedDto.fromEnum(CodeUserRoleEnum.getUserRole(obj.getRoleList())))
				.build();
	}

}
