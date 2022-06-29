
package io.zeitwert.ddd.obj.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoBridge;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ObjPartTransitionDto {

	private Integer seqNr;

	private ObjUserDto user;

	private OffsetDateTime timestamp;

	public static ObjPartTransitionDto fromPart(ObjPartTransition transition, SessionInfo sessionInfo) {
		ObjUserDtoBridge userBridge = ObjUserDtoBridge.getInstance();
		// @formatter:off
		return ObjPartTransitionDto.builder()
			.seqNr(transition.getSeqNr())
			.user(userBridge.fromAggregate(transition.getUser(), sessionInfo))
			.timestamp(transition.getTimestamp())
			.build();
		// @formatter:on
	}

}
