package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Record;

import io.crnk.core.resource.meta.MetaInformation;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateMeta;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.validation.adapter.api.jsonapi.dto.AggregatePartValidationDto;
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
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		builder
			.itemType(EnumeratedDto.fromEnum(meta.getAggregateType()))
			.owner(userDtoAdapter.asEnumerated(aggregate.getOwner()))
			.version(meta.getVersion())
			.createdByUser(userDtoAdapter.asEnumerated(meta.getCreatedByUser()))
			.createdAt(meta.getCreatedAt())
			.modifiedByUser(userDtoAdapter.asEnumerated(meta.getModifiedByUser()))
			.modifiedAt(meta.getModifiedAt())
			.validations(meta.getValidations().stream().map(v -> AggregatePartValidationDto.fromValidation(v)).toList())
			.build();
		// @formatter:on
	}

	public static void fromRecord(AggregateMetaDtoBuilder<?, ?> builder, Record aggregate) {
		// @formatter:off
		builder
			.build();
		// @formatter:on
	}

}
