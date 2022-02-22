
package fm.comunas.ddd.obj.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import io.crnk.core.resource.meta.MetaInformation;
import fm.comunas.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjMeta;
import fm.comunas.ddd.obj.model.base.ObjFields;
import fm.comunas.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import fm.comunas.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.ddd.validation.adapter.api.jsonapi.dto.AggregatePartValidationDto;
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
			.validationList(meta.getValidationList().stream().map(v -> AggregatePartValidationDto.fromValidation(v)).toList())
			.build();
		// @formatter:on
	}

	public static ObjMetaDto fromRecord(Record obj, SessionInfo sessionInfo) {
		// @formatter:off
		return ObjMetaDto.builder()
			.sessionId(sessionInfo.getId())
			.itemType(EnumeratedDto.fromEnum(CodeAggregateTypeEnum.getAggregateType(obj.get(ObjFields.OBJ_TYPE_ID))))
			.tenant(ObjTenantDto.fromObj(sessionInfo.getTenant()))
			//.owner(ObjUserDto.fromObj(obj.getOwner()))
			//.createdByUser(ObjUserDto.fromObj(meta.getCreatedByUser()))
			.createdAt(obj.get(ObjFields.CREATED_AT))
			//.modifiedByUser(ObjUserDto.fromObj(meta.getModifiedByUser()))
			.modifiedAt(obj.get(ObjFields.MODIFIED_AT))
			.build();
		// @formatter:on
	}

}
