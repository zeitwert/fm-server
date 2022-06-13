
package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class DocPartTransitionDto {

	private Integer seqNr;

	private ObjUserDto user;

	private OffsetDateTime modifiedAt;

	private CodeCaseStage oldCaseStage;

	private CodeCaseStage newCaseStage;

	public static DocPartTransitionDto fromPart(DocPartTransition transition) {
		// @formatter:off
		return DocPartTransitionDto.builder()
			.seqNr(transition.getSeqNr())
			.user(ObjUserDto.fromObj(transition.getUser()))
			.modifiedAt(transition.getModifiedAt())
			.oldCaseStage(transition.getOldCaseStage())
			.newCaseStage(transition.getNewCaseStage())
			.build();
		// @formatter:on
	}

}
