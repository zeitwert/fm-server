
package io.zeitwert.ddd.obj.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import io.crnk.core.resource.meta.MetaInformation;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjMeta;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.validation.adapter.api.jsonapi.dto.AggregatePartValidationDto;
import lombok.Builder;
import lombok.Data;
import org.jooq.Record;

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
		// @formatter:off
		return ObjMetaDto.builder()
			.sessionId(sessionInfo.getId())
			.itemType(EnumeratedDto.fromEnum(obj.getRepository().getAggregateType()))
			.tenant(ObjTenantDto.fromObj(obj.getTenant()))
			.owner(ObjUserDto.fromObj(obj.getOwner()))
			.createdByUser(ObjUserDto.fromObj(meta.getCreatedByUser()))
			.createdAt(meta.getCreatedAt())
			.modifiedByUser(ObjUserDto.fromObj(meta.getModifiedByUser()))
			.modifiedAt(meta.getModifiedAt())
			.closedByUser(ObjUserDto.fromObj(meta.getClosedByUser()))
			.closedAt(meta.getClosedAt())
			.transitionList(meta.getTransitionList().stream().map(t -> ObjPartTransitionDto.fromPart(t)).toList())
			.validationList(meta.getValidationList().stream().map(v -> AggregatePartValidationDto.fromValidation(v)).toList())
			.build();
		// @formatter:on
	}

	public static ObjMetaDto fromRecord(Record obj, SessionInfo sessionInfo) {
		ObjTenantRepository tenantRepo = (ObjTenantRepository) AppContext.getInstance().getRepository(ObjTenant.class);
		ObjUserRepository userRepo = (ObjUserRepository) AppContext.getInstance().getRepository(ObjUser.class);
		Integer modifiedByUserId = obj.getValue(ObjFields.MODIFIED_BY_USER_ID);
		ObjUserDto modifiedByUser = modifiedByUserId == null ? null : ObjUserDto.fromObj(userRepo.get(modifiedByUserId));
		Integer closedByUserId = obj.getValue(ObjFields.CLOSED_BY_USER_ID);
		ObjUserDto closedByUser = closedByUserId == null ? null : ObjUserDto.fromObj(userRepo.get(closedByUserId));
		// @formatter:off
		return ObjMetaDto.builder()
			.sessionId(sessionInfo.getId())
			.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(obj.get(ObjFields.OBJ_TYPE_ID))))
			.tenant(ObjTenantDto.fromObj(tenantRepo.get(obj.getValue(ObjFields.TENANT_ID))))
			.owner(ObjUserDto.fromObj(userRepo.get(obj.getValue(ObjFields.OWNER_ID))))
			.createdByUser(ObjUserDto.fromObj(userRepo.get(obj.getValue(ObjFields.CREATED_BY_USER_ID))))
			.createdAt(obj.get(ObjFields.CREATED_AT))
			.modifiedByUser(modifiedByUser)
			.modifiedAt(obj.get(ObjFields.MODIFIED_AT))
			.closedByUser(closedByUser)
			.closedAt(obj.get(ObjFields.CLOSED_AT))
			.build();
		// @formatter:on
	}

}
