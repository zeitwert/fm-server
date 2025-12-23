package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto;

import io.crnk.core.resource.meta.MetaInformation;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.AggregateMeta;
import io.zeitwert.dddrive.ddd.api.rest.dto.AggregatePartValidationDto;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

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

	public static void fromAggregate(AggregateMetaDtoBuilder<?, ?> builder, Aggregate aggregate) {
		AggregateMeta meta = aggregate.getMeta();
		// @formatter:off
		builder
			.itemType(EnumeratedDto.of(meta.getRepository().getAggregateType()))
			.owner(EnumeratedDto.of(aggregate.getOwner()))
			.version(meta.getVersion())
			.createdByUser(EnumeratedDto.of(meta.getCreatedByUser()))
			.createdAt(meta.getCreatedAt())
			.modifiedByUser(EnumeratedDto.of(meta.getModifiedByUser()))
			.modifiedAt(meta.getModifiedAt())
			.validations(meta.getValidations().stream().map(AggregatePartValidationDto::fromValidation).toList());
		// @formatter:on
	}

	public boolean hasOperation(String operation) {
		return this.operations != null && this.operations.contains(operation);
	}

}
