
package io.zeitwert.ddd.obj.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.session.model.RequestContext;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ObjPartTransitionDto {

	private Integer seqNr;

	private EnumeratedDto user;

	private OffsetDateTime timestamp;

	public static ObjPartTransitionDto fromPart(ObjPartTransition transition, RequestContext requestCtx) {
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		return ObjPartTransitionDto.builder()
			.seqNr(transition.getSeqNr())
			.user(userDtoAdapter.asEnumerated(transition.getUser(), requestCtx))
			.timestamp(transition.getTimestamp())
			.build();
		// @formatter:on
	}

}
