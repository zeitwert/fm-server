package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto;

import dddrive.app.doc.model.Doc;
import dddrive.app.doc.model.DocMeta;
import dddrive.app.obj.model.Obj;
import dddrive.app.obj.model.ObjMeta;
import io.crnk.core.resource.meta.MetaInformation;
import io.zeitwert.dddrive.ddd.api.rest.dto.AggregatePartValidationDto;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.oe.model.ObjUserRepository;
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

	public static void fromObj(AggregateMetaDtoBuilder<?, ?> builder, Obj obj, ObjUserRepository userRepo) {
		ObjMeta meta = obj.getMeta();
		// @formatter:off
		builder
				.itemType(EnumeratedDto.of(meta.getRepository().getAggregateType()))
				.owner(EnumeratedDto.of(obj.getOwnerId() != null ? userRepo.get(obj.getOwnerId()) : null))
				.version(meta.getVersion())
				.createdByUser(EnumeratedDto.of(userRepo.get(meta.getCreatedByUserId())))
				.createdAt(meta.getCreatedAt())
				.modifiedByUser(EnumeratedDto.of(meta.getModifiedByUserId() != null ? userRepo.get(meta.getModifiedByUserId()) : null))
				.modifiedAt(meta.getModifiedAt())
				.validations(meta.getValidationList().stream().map(AggregatePartValidationDto::fromValidation).toList());
		// @formatter:on
	}

	public static void fromDoc(AggregateMetaDtoBuilder<?, ?> builder, Doc doc, ObjUserRepository userRepo) {
		DocMeta meta = doc.getMeta();
		// @formatter:off
		builder
				.itemType(EnumeratedDto.of(meta.getRepository().getAggregateType()))
				.owner(EnumeratedDto.of(doc.getOwnerId() != null ? userRepo.get(doc.getOwnerId()) : null))
				.version(meta.getVersion())
				.createdByUser(EnumeratedDto.of(userRepo.get(meta.getCreatedByUserId())))
				.createdAt(meta.getCreatedAt())
				.modifiedByUser(EnumeratedDto.of(meta.getModifiedByUserId() != null ? userRepo.get(meta.getModifiedByUserId()) : null))
				.modifiedAt(meta.getModifiedAt())
				.validations(meta.getValidationList().stream().map(AggregatePartValidationDto::fromValidation).toList());
		// @formatter:on
	}

	public boolean hasOperation(String operation) {
		return this.operations != null && this.operations.contains(operation);
	}

}
