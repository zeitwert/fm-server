
package io.zeitwert.ddd.obj.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Record;

import io.crnk.core.resource.meta.MetaInformation;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjMeta;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoBridge;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoBridge;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.validation.adapter.api.jsonapi.dto.AggregatePartValidationDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObjMetaDto implements MetaInformation {

	private Integer sessionId;
	private EnumeratedDto itemType;
	private ObjTenantDto tenant;
	private ObjUserDto owner;
	private ObjUserDto createdByUser;
	private OffsetDateTime createdAt;
	private ObjUserDto modifiedByUser;
	private OffsetDateTime modifiedAt;
	private ObjUserDto closedByUser;
	private OffsetDateTime closedAt;
	private List<ObjPartTransitionDto> transitionList;
	private List<AggregatePartValidationDto> validationList;

	public static ObjMetaDto fromObj(Obj obj, SessionInfo sessionInfo) {
		ObjMeta meta = obj.getMeta();
		ObjTenantDtoBridge tenantBridge = ObjTenantDtoBridge.getInstance();
		ObjUserDtoBridge userBridge = ObjUserDtoBridge.getInstance();
		// @formatter:off
		return ObjMetaDto.builder()
			.sessionId(sessionInfo.getId())
			.itemType(EnumeratedDto.fromEnum(obj.getMeta().getAggregateType()))
			.tenant(tenantBridge.fromAggregate(obj.getTenant(), sessionInfo))
			.owner(userBridge.fromAggregate(obj.getOwner(), sessionInfo))
			.createdByUser(userBridge.fromAggregate(meta.getCreatedByUser(), sessionInfo))
			.createdAt(meta.getCreatedAt())
			.modifiedByUser(userBridge.fromAggregate(meta.getModifiedByUser(), sessionInfo))
			.modifiedAt(meta.getModifiedAt())
			.closedByUser(userBridge.fromAggregate(meta.getClosedByUser(), sessionInfo))
			.closedAt(meta.getClosedAt())
			.transitionList(meta.getTransitionList().stream().map(t -> ObjPartTransitionDto.fromPart(t, sessionInfo)).toList())
			.validationList(meta.getValidationList().stream().map(v -> AggregatePartValidationDto.fromValidation(v)).toList())
			.build();
		// @formatter:on
	}

	public static ObjMetaDto fromRecord(Record obj, SessionInfo sessionInfo) {
		ObjTenantRepository tenantRepo = (ObjTenantRepository) AppContext.getInstance().getRepository(ObjTenant.class);
		ObjUserRepository userRepo = (ObjUserRepository) AppContext.getInstance().getRepository(ObjUser.class);
		ObjTenantDtoBridge tenantBridge = ObjTenantDtoBridge.getInstance();
		ObjUserDtoBridge userBridge = ObjUserDtoBridge.getInstance();
		Integer modifiedByUserId = obj.getValue(ObjFields.MODIFIED_BY_USER_ID);
		ObjUserDto modifiedByUser = modifiedByUserId == null ? null
				: userBridge.fromAggregate(userRepo.get(modifiedByUserId), sessionInfo);
		Integer closedByUserId = obj.getValue(ObjFields.CLOSED_BY_USER_ID);
		ObjUserDto closedByUser = closedByUserId == null ? null
				: userBridge.fromAggregate(userRepo.get(closedByUserId), sessionInfo);
		// @formatter:off
		return ObjMetaDto.builder()
			.sessionId(sessionInfo.getId())
			.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(obj.get(ObjFields.OBJ_TYPE_ID))))
			.tenant(tenantBridge.fromAggregate(tenantRepo.get(obj.getValue(ObjFields.TENANT_ID)), sessionInfo))
			.owner(userBridge.fromAggregate(userRepo.get(obj.getValue(ObjFields.OWNER_ID)), sessionInfo))
			.createdByUser(userBridge.fromAggregate(userRepo.get(obj.getValue(ObjFields.CREATED_BY_USER_ID)), sessionInfo))
			.createdAt(obj.get(ObjFields.CREATED_AT))
			.modifiedByUser(modifiedByUser)
			.modifiedAt(obj.get(ObjFields.MODIFIED_AT))
			.closedByUser(closedByUser)
			.closedAt(obj.get(ObjFields.CLOSED_AT))
			.build();
		// @formatter:on
	}

}
