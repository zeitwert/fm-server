
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
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
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

	private EnumeratedDto closedByUser;
	private OffsetDateTime closedAt;
	private List<ObjPartTransitionDto> transitionList;

	public static ObjMetaDto fromObj(Obj obj, SessionInfo sessionInfo) {
		ObjMeta meta = obj.getMeta();
		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		AggregateMetaDto.fromAggregate(builder, obj, sessionInfo);
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		return builder
			.closedByUser(userDtoAdapter.asEnumerated(meta.getClosedByUser(), sessionInfo))
			.closedAt(meta.getClosedAt())
			.transitionList(meta.getTransitionList().stream().map(t -> ObjPartTransitionDto.fromPart(t, sessionInfo)).toList())
			.build();
		// @formatter:on
	}

	public static ObjMetaDto fromRecord(Record obj, SessionInfo sessionInfo) {
		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		AggregateMetaDto.fromRecord(builder, obj, sessionInfo);
		ObjUserRepository userRepo = (ObjUserRepository) AppContext.getInstance().getRepository(ObjUser.class);
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		Integer modifiedByUserId = obj.getValue(ObjFields.MODIFIED_BY_USER_ID);
		EnumeratedDto modifiedByUser = modifiedByUserId == null ? null
				: userDtoAdapter.asEnumerated(userRepo.get(sessionInfo, modifiedByUserId), sessionInfo);
		Integer closedByUserId = obj.getValue(ObjFields.CLOSED_BY_USER_ID);
		EnumeratedDto closedByUser = closedByUserId == null ? null
				: userDtoAdapter.asEnumerated(userRepo.get(sessionInfo, closedByUserId), sessionInfo);
		// @formatter:off
		return builder
			.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(obj.get(ObjFields.OBJ_TYPE_ID))))
			.owner(userDtoAdapter.asEnumerated(userRepo.get(sessionInfo, obj.getValue(ObjFields.OWNER_ID)), sessionInfo))
			.createdByUser(userDtoAdapter.asEnumerated(userRepo.get(sessionInfo, obj.getValue(ObjFields.CREATED_BY_USER_ID)), sessionInfo))
			.createdAt(obj.get(ObjFields.CREATED_AT))
			.modifiedByUser(modifiedByUser)
			.modifiedAt(obj.get(ObjFields.MODIFIED_AT))
			.closedByUser(closedByUser)
			.closedAt(obj.get(ObjFields.CLOSED_AT))
			.build();
		// @formatter:on
	}

}
