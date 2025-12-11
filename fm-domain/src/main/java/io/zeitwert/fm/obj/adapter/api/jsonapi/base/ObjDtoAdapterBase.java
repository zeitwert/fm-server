
package io.zeitwert.fm.obj.adapter.api.jsonapi.base;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateDtoAdapterBase;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjMeta;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjMetaDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartTransitionDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjMetaDto.ObjMetaDtoBuilder;
import io.zeitwert.fm.obj.model.base.ObjFields;

import org.jooq.TableRecord;

public abstract class ObjDtoAdapterBase<O extends Obj, V extends Object, D extends ObjDtoBase<O>>
		extends AggregateDtoAdapterBase<O, V, D> {

	@Override
	public void toAggregate(D dto, O obj) {
		if (dto.getOwner() != null) {
			obj.setOwner(this.getUser(Integer.parseInt(dto.getOwner().getId())));
		}
	}

	protected void fromAggregate(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, O obj) {
		dtoBuilder
				.adapter(this)
				.tenant(EnumeratedDto.fromAggregate(obj.getTenant()))
				.meta(this.metaFromObj(obj))
				.id(obj.getId())
				.caption(obj.getCaption())
				.owner(EnumeratedDto.fromAggregate(obj.getOwner()));
	}

	private ObjMetaDto metaFromObj(Obj obj) {
		ObjMeta meta = obj.getMeta();
		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		AggregateMetaDto.fromAggregate(builder, obj);
		return builder
				.closedByUser(EnumeratedDto.fromAggregate(meta.getClosedByUser()))
				.closedAt(meta.getClosedAt())
				.transitions(meta.getTransitionList().stream().map(t -> ObjPartTransitionDto.fromPart(t)).toList())
				.build();
	}

	protected void fromRecord(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> obj) {
		EnumeratedDto tenant = this.getTenantEnumerated(obj.get(ObjFields.TENANT_ID));
		EnumeratedDto owner = this.getUserEnumerated(obj.get(ObjFields.OWNER_ID));
		dtoBuilder
				.adapter(this)
				.tenant(tenant)
				.meta(this.metaFromRecord(obj))
				.id(obj.get(ObjFields.ID))
				.caption(obj.get(ObjFields.CAPTION))
				.owner(owner);
	}

	private ObjMetaDto metaFromRecord(TableRecord<?> obj) {
		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		return builder
				.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(obj.get(ObjFields.OBJ_TYPE_ID))))
				.owner(this.getUserEnumerated(obj.getValue(ObjFields.OWNER_ID)))
				.version(obj.get(ObjFields.VERSION))
				.createdByUser(this.getUserEnumerated(obj.getValue(ObjFields.CREATED_BY_USER_ID)))
				.createdAt(obj.get(ObjFields.CREATED_AT))
				.modifiedByUser(this.getUserEnumerated(obj.getValue(ObjFields.MODIFIED_BY_USER_ID)))
				.modifiedAt(obj.get(ObjFields.MODIFIED_AT))
				.closedByUser(this.getUserEnumerated(obj.getValue(ObjFields.CLOSED_BY_USER_ID)))
				.closedAt(obj.get(ObjFields.CLOSED_AT))
				.build();
	}

}
