
package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.dddrive.obj.model.ObjPartTransition;
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
				.seqNr(transition.getSeqNr())
				.user(EnumeratedDto.fromAggregate(transition.getUser()))
				.timestamp(transition.getTimestamp())
				.build();
	}

}
