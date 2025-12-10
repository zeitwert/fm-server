
package io.zeitwert.fm.doc.adapter.api.jsonapi.dto;

import io.dddrive.doc.model.DocPartTransition;
import io.dddrive.doc.model.enums.CodeCaseStage;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class DocPartTransitionDto {

	private Integer seqNr;

	private EnumeratedDto user;

	private OffsetDateTime timestamp;

	private CodeCaseStage oldCaseStage;

	private CodeCaseStage newCaseStage;

	public static DocPartTransitionDto fromPart(DocPartTransition transition) {
		return DocPartTransitionDto.builder()
				.seqNr(transition.getSeqNr())
				.user(EnumeratedDto.fromAggregate(transition.getUser()))
				.timestamp(transition.getTimestamp())
				.oldCaseStage(transition.getOldCaseStage())
				.newCaseStage(transition.getNewCaseStage())
				.build();
	}

}
