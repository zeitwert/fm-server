
package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.fm.oe.model.ObjUserFM;
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
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		return ObjPartTransitionDto.builder()
				.seqNr(transition.getSeqNr())
				.user(userDtoAdapter.asEnumerated((ObjUserFM) transition.getUser()))
				.timestamp(transition.getTimestamp())
				.build();
	}

}
