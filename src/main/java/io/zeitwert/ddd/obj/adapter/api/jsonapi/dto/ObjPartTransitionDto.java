
package io.zeitwert.ddd.obj.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@SuperBuilder
@JsonApiResource(type = "objPartTransition", resourcePath = "obj/transitions", nested = true, patchable = false, deletable = false)
public class ObjPartTransitionDto {

	@JsonApiId
	private ObjPartId id;

	@JsonApiRelation(opposite = "transitions")
	private ObjDto obj;

	private Integer seqNr;

	private ObjUserDto user;

	private OffsetDateTime modifiedAt;

	public static ObjPartTransitionDto fromPart(ObjPartTransition transition) {
		// @formatter:off
		return ObjPartTransitionDto.builder()
			.id(new ObjPartId(transition.getMeta().getAggregate().getId(), transition.getId()))
			.seqNr(transition.getSeqNr())
			.user(ObjUserDto.fromObj(transition.getUser()))
			.modifiedAt(transition.getModifiedAt())
			.build();
		// @formatter:on
	}

}
