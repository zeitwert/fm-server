
package io.zeitwert.ddd.doc.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.session.model.SessionInfo;
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

	public static DocPartTransitionDto fromPart(DocPartTransition transition, SessionInfo sessionInfo) {
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		return DocPartTransitionDto.builder()
			.seqNr(transition.getSeqNr())
			.user(userDtoAdapter.asEnumerated(transition.getUser(), sessionInfo))
			.timestamp(transition.getTimestamp())
			.oldCaseStage(transition.getOldCaseStage())
			.newCaseStage(transition.getNewCaseStage())
			.build();
		// @formatter:on
	}

}
