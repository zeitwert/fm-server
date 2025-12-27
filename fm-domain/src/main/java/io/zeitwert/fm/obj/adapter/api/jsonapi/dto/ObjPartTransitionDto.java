package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import dddrive.app.obj.model.ObjPartTransition;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ObjPartTransitionDto {

	private Integer seqNr;

	private EnumeratedDto user;

	private OffsetDateTime timestamp;

	public static ObjPartTransitionDto fromPart(ObjPartTransition transition, ObjUserRepository userRepo) {
		return ObjPartTransitionDto.builder()
//				.seqNr(transition.getSeqNr()) TODO-MIGRATION
				.user(EnumeratedDto.of(userRepo.get(transition.getUserId())))
				.timestamp(transition.getTimestamp())
				.build();
	}

}
