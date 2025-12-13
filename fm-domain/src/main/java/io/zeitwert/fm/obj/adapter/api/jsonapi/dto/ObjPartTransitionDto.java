
package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.dddrive.core.obj.model.ObjPartTransition;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ObjPartTransitionDto {

	private Integer seqNr;

	private EnumeratedDto user;

	private OffsetDateTime timestamp;

	public static ObjPartTransitionDto fromPart(ObjPartTransition transition) {
		return ObjPartTransitionDto.builder()
//				.seqNr(transition.getSeqNr()) TODO-MIGRATION
				.user(EnumeratedDto.of(transition.getUser()))
				.timestamp(transition.getTimestamp())
				.build();
	}

}
