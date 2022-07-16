
package io.zeitwert.ddd.obj.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Record;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjMeta;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ObjMetaDto extends AggregateMetaDto {

	private ObjUserDto closedByUser;
	private OffsetDateTime closedAt;
	private List<ObjPartTransitionDto> transitionList;

	public static ObjMetaDto fromObj(Obj obj, SessionInfo sessionInfo) {
		ObjMeta meta = obj.getMeta();
		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		AggregateMetaDto.fromAggregate(builder, obj, sessionInfo);
		ObjUserDtoAdapter userBridge = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		return builder
			.closedByUser(userBridge.fromAggregate(meta.getClosedByUser(), sessionInfo))
			.closedAt(meta.getClosedAt())
			.transitionList(meta.getTransitionList().stream().map(t -> ObjPartTransitionDto.fromPart(t, sessionInfo)).toList())
			.build();
		// @formatter:on
	}

	public static ObjMetaDto fromRecord(Record obj, SessionInfo sessionInfo) {
		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		AggregateMetaDto.fromRecord(builder, obj, sessionInfo);
		ObjTenantRepository tenantRepo = (ObjTenantRepository) AppContext.getInstance().getRepository(ObjTenant.class);
		ObjUserRepository userRepo = (ObjUserRepository) AppContext.getInstance().getRepository(ObjUser.class);
		ObjTenantDtoAdapter tenantBridge = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userBridge = ObjUserDtoAdapter.getInstance();
		Integer modifiedByUserId = obj.getValue(ObjFields.MODIFIED_BY_USER_ID);
		ObjUserDto modifiedByUser = modifiedByUserId == null ? null
				: userBridge.fromAggregate(userRepo.get(modifiedByUserId), sessionInfo);
		Integer closedByUserId = obj.getValue(ObjFields.CLOSED_BY_USER_ID);
		ObjUserDto closedByUser = closedByUserId == null ? null
				: userBridge.fromAggregate(userRepo.get(closedByUserId), sessionInfo);
		// @formatter:off
		return builder
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
