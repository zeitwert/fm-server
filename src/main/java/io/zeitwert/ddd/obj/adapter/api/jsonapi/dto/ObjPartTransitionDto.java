
package io.zeitwert.ddd.obj.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ObjPartTransitionDto {

	private Integer seqNr;

	private ObjUserDto user;

	private OffsetDateTime modifiedAt;

	public static ObjPartTransitionDto fromPart(ObjPartTransition transition) {
		// @formatter:off
		return ObjPartTransitionDto.builder()
			.seqNr(transition.getSeqNr())
			.user(ObjUserDto.fromObj(transition.getUser()))
			.modifiedAt(transition.getModifiedAt())
			.build();
		// @formatter:on
	}

}
