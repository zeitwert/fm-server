
package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Record;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjMeta;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.jooq.property.ObjFields;
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
	private List<ObjPartTransitionDto> transitions;

	public static ObjMetaDto fromObj(Obj obj) {
		ObjMeta meta = obj.getMeta();
		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		AggregateMetaDto.fromAggregate(builder, obj);
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		return builder
				.closedByUser(userDtoAdapter.asEnumerated((ObjUserFM) meta.getClosedByUser()))
				.closedAt(meta.getClosedAt())
				.transitions(meta.getTransitionList().stream().map(t -> ObjPartTransitionDto.fromPart(t)).toList())
				.build();
	}

	public static ObjMetaDto fromRecord(Record obj) {
		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		AggregateMetaDto.fromRecord(builder, obj);
		ObjUserCache userCache = (ObjUserCache) AppContext.getInstance().getBean(ObjUserCache.class);
		Integer modifiedByUserId = obj.getValue(ObjFields.MODIFIED_BY_USER_ID);
		EnumeratedDto modifiedByUser = modifiedByUserId == null ? null : userCache.getAsEnumerated(modifiedByUserId);
		Integer closedByUserId = obj.getValue(ObjFields.CLOSED_BY_USER_ID);
		EnumeratedDto closedByUser = closedByUserId == null ? null : userCache.getAsEnumerated(closedByUserId);
		return builder
				.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(obj.get(ObjFields.OBJ_TYPE_ID))))
				.owner(userCache.getAsEnumerated(obj.getValue(ObjFields.OWNER_ID)))
				.version(obj.get(ObjFields.VERSION))
				.createdByUser(userCache.getAsEnumerated(obj.getValue(ObjFields.CREATED_BY_USER_ID)))
				.createdAt(obj.get(ObjFields.CREATED_AT))
				.modifiedByUser(modifiedByUser)
				.modifiedAt(obj.get(ObjFields.MODIFIED_AT))
				.closedByUser(closedByUser)
				.closedAt(obj.get(ObjFields.CLOSED_AT))
				.build();
	}

}
