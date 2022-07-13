package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Record;

import io.crnk.core.resource.meta.MetaInformation;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateMeta;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoBridge;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoBridge;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.validation.adapter.api.jsonapi.dto.AggregatePartValidationDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class AggregateMetaDto implements MetaInformation {

	// Meta from Server
	private Integer sessionId;
	private EnumeratedDto itemType;
	private ObjTenantDto tenant;
	private ObjUserDto owner;
	private Integer version;
	private ObjUserDto createdByUser;
	private OffsetDateTime createdAt;
	private ObjUserDto modifiedByUser;
	private OffsetDateTime modifiedAt;
	private List<AggregatePartValidationDto> validationList;

	// Meta from Client
	private Integer clientVersion;
	private List<String> operationList;

	public boolean hasOperation(String operation) {
		return this.operationList != null && this.operationList.contains(operation);
	}

	public static void fromAggregate(AggregateMetaDtoBuilder<?, ?> builder, Aggregate aggregate,
			SessionInfo sessionInfo) {
		AggregateMeta meta = aggregate.getMeta();
		ObjTenantDtoBridge tenantBridge = ObjTenantDtoBridge.getInstance();
		ObjUserDtoBridge userBridge = ObjUserDtoBridge.getInstance();
		// @formatter:off
		builder
			.sessionId(sessionInfo.getId())
			.itemType(EnumeratedDto.fromEnum(aggregate.getMeta().getAggregateType()))
			.tenant(tenantBridge.fromAggregate(aggregate.getTenant(), sessionInfo))
			.owner(userBridge.fromAggregate(aggregate.getOwner(), sessionInfo))
			.version(meta.getVersion())
			.createdByUser(userBridge.fromAggregate(meta.getCreatedByUser(), sessionInfo))
			.createdAt(meta.getCreatedAt())
			.modifiedByUser(userBridge.fromAggregate(meta.getModifiedByUser(), sessionInfo))
			.modifiedAt(meta.getModifiedAt())
			.validationList(meta.getValidationList().stream().map(v -> AggregatePartValidationDto.fromValidation(v)).toList())
			.build();
		// @formatter:on
	}

	public static void fromRecord(AggregateMetaDtoBuilder<?, ?> builder, Record aggregate, SessionInfo sessionInfo) {
		// @formatter:off
		builder
			.sessionId(sessionInfo.getId())
			.build();
		// @formatter:on
	}

}
