
package fm.comunas.ddd.doc.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import fm.comunas.ddd.doc.model.DocPartTransition;
import fm.comunas.ddd.doc.model.enums.CodeCaseStage;
import fm.comunas.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@SuperBuilder
@JsonApiResource(type = "docPartTransition", resourcePath = "doc/transitions", nested = true, patchable = false, deletable = false)
public class DocPartTransitionDto {

	@JsonApiId
	private DocPartId id;

	@JsonApiRelation(opposite = "transitions")
	private DocDto doc;

	private Integer seqNr;

	private ObjUserDto user;

	private OffsetDateTime modifiedAt;

	private CodeCaseStage oldCaseStage;

	private CodeCaseStage newCaseStage;

	public static DocPartTransitionDto fromPart(DocPartTransition transition) {
		// @formatter:off
		return DocPartTransitionDto.builder()
			.id(new DocPartId(transition.getMeta().getAggregate().getId(), transition.getId()))
			.seqNr(transition.getSeqNr())
			.user(ObjUserDto.fromObj(transition.getUser()))
			.modifiedAt(transition.getModifiedAt())
			.oldCaseStage(transition.getOldCaseStage())
			.newCaseStage(transition.getNewCaseStage())
			.build();
		// @formatter:on
	}

}
