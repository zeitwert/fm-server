package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.oe.model.ObjUser;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.enums.CodeUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component("objUserDtoAdapter")
public class ObjUserDtoAdapter extends ObjDtoAdapterBase<ObjUserFM, ObjUserDto> {

	private static EnumeratedDto AGGREGATE_TYPE;

	private ObjDocumentRepository documentRepository = null;
	private ObjDocumentDtoAdapter documentDtoAdapter = null;

	protected ObjUserDtoAdapter() {
		AGGREGATE_TYPE = EnumeratedDto.of(CodeAggregateTypeEnum.getAggregateType("obj_user"));
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

	public OffsetDateTime getLastTouch(Integer id) {
		return null; // this.getUserRepository().getLastTouch(id);
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
			obj.setRole(CodeUserRole.getUserRole(dto.getRole().getId()));
			obj.clearTenantSet();
			for (EnumeratedDto tenant : dto.getTenants()) {
				obj.addTenant(Integer.parseInt(tenant.getId()));
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
				.role(EnumeratedDto.of(obj.getRole()))
				.tenants(obj.getTenantSet().stream().map(id -> this.getTenantEnumerated((Integer) id)).toList())
				.needPasswordChange(obj.getNeedPasswordChange())
				.avatarId((Integer) obj.getAvatarImageId())
				.build();
	}

	public EnumeratedDto asEnumerated(ObjUser obj) {
		if (obj == null) {
			return null;
		}
		return EnumeratedDto.of("" + obj.getId(), obj.getCaption());
//		return EnumeratedDto.builder() TODO-MIGRATION
//				.id("" + obj.getId())
//				.itemType(AGGREGATE_TYPE)
//				.name(obj.getCaption())
//				.build();
	}

//	@Override
//	public ObjUserDto fromRecord(ObjUserVRecord obj) {
//		if (obj == null) {
//			return null;
//		}
//		ObjUserDto.ObjUserDtoBuilder<?, ?> dtoBuilder = ObjUserDto.builder();
//		this.fromRecord(dtoBuilder, obj);
//		return dtoBuilder
//				.email(obj.getEmail())
//				.name(obj.getName())
//				.description(obj.getDescription())
//				.role(EnumeratedDto.of(CodeUserRole.getUserRole(obj.getRoleList())))
//				.avatarId(obj.getAvatarImgId())
//				.build();
//	}

}
