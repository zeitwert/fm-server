package io.zeitwert.fm.obj.adapter.api.jsonapi.base;

import dddrive.app.obj.model.Obj;
import dddrive.app.obj.model.ObjMeta;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateDtoAdapterBase;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjMetaDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjPartTransitionDto;

public abstract class ObjDtoAdapterBase<O extends Obj, D extends ObjDtoBase<O>>
		extends AggregateDtoAdapterBase<O, D> {

	@Override
	public void toAggregate(D dto, O obj) {
		if (dto.getOwner() != null) {
			obj.setOwner(this.getUser(Integer.parseInt(dto.getOwner().getId())));
		}
	}

	protected void fromAggregate(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, O obj) {
		dtoBuilder
				.adapter(this)
				.tenant(EnumeratedDto.of(obj.getTenant()))
				.meta(this.metaFromObj(obj))
				.id((Integer) obj.getId())
				.caption(obj.getCaption())
				.owner(EnumeratedDto.of(obj.getOwner()));
	}

	private ObjMetaDto metaFromObj(Obj obj) {
		ObjMeta meta = obj.getMeta();
		ObjMetaDto.ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
		AggregateMetaDto.fromAggregate(builder, obj);
		return builder
				.closedByUser(EnumeratedDto.of(meta.getClosedByUser()))
				.closedAt(meta.getClosedAt())
				.transitions(meta.getTransitionList().stream().map(ObjPartTransitionDto::fromPart).toList())
				.build();
	}

//	protected void fromRecord(ObjDtoBase.ObjDtoBaseBuilder<?, ?, ?> dtoBuilder, TableRecord<?> obj) {
//		EnumeratedDto tenant = this.getTenantEnumerated(obj.get(ObjFields.TENANT_ID));
//		EnumeratedDto owner = this.getUserEnumerated(obj.get(ObjFields.OWNER_ID));
//		dtoBuilder
//				.adapter(this)
//				.tenant(tenant)
//				.meta(this.metaFromRecord(obj))
//				.id(obj.get(ObjFields.ID))
//				.caption(obj.get(ObjFields.CAPTION))
//				.owner(owner);
//	}

//	private ObjMetaDto metaFromRecord(TableRecord<?> obj) {
//		ObjMetaDtoBuilder<?, ?> builder = ObjMetaDto.builder();
//		return builder
//				.itemType(EnumeratedDto.of(CodeAggregateTypeEnum.getAggregateType(obj.get(ObjFields.OBJ_TYPE_ID))))
//				.owner(this.getUserEnumerated(obj.getValue(ObjFields.OWNER_ID)))
//				.version(obj.get(ObjFields.VERSION))
//				.createdByUser(this.getUserEnumerated(obj.getValue(ObjFields.CREATED_BY_USER_ID)))
//				.createdAt(obj.get(ObjFields.CREATED_AT))
//				.modifiedByUser(this.getUserEnumerated(obj.getValue(ObjFields.MODIFIED_BY_USER_ID)))
//				.modifiedAt(obj.get(ObjFields.MODIFIED_AT))
//				.closedByUser(this.getUserEnumerated(obj.getValue(ObjFields.CLOSED_BY_USER_ID)))
//				.closedAt(obj.get(ObjFields.CLOSED_AT))
//				.build();
//	}

}
