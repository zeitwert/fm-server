package io.dddrive.ddd.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import io.crnk.core.resource.meta.MetaInformation;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.AggregateMeta;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.dddrive.validation.adapter.api.jsonapi.dto.AggregatePartValidationDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class AggregateMetaDto implements MetaInformation {

	// Meta from Server
	private EnumeratedDto itemType;
	private EnumeratedDto owner;
	private Integer version;
	private EnumeratedDto createdByUser;
	private OffsetDateTime createdAt;
	private EnumeratedDto modifiedByUser;
	private OffsetDateTime modifiedAt;
	private List<AggregatePartValidationDto> validations;

	// Meta from Client
	private Integer clientVersion;
	private List<String> operations;

	public boolean hasOperation(String operation) {
		return this.operations != null && this.operations.contains(operation);
	}

	public static void fromAggregate(AggregateMetaDtoBuilder<?, ?> builder, Aggregate aggregate) {
		AggregateMeta meta = aggregate.getMeta();
		// @formatter:off
		builder
			.itemType(EnumeratedDto.fromEnum(meta.getAggregateType()))
			.owner(EnumeratedDto.fromAggregate(aggregate.getOwner()))
			.version(meta.getVersion())
			.createdByUser(EnumeratedDto.fromAggregate(meta.getCreatedByUser()))
			.createdAt(meta.getCreatedAt())
			.modifiedByUser(EnumeratedDto.fromAggregate(meta.getModifiedByUser()))
			.modifiedAt(meta.getModifiedAt())
			.validations(meta.getValidations().stream().map(v -> AggregatePartValidationDto.fromValidation(v)).toList())
			.build();
		// @formatter:on
	}

}
