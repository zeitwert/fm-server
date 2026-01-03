package io.zeitwert.fm.app.adapter.api.rest.dto;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class HomeActionResponse {

	private EnumeratedDto item;
	private Integer seqNr;
	private OffsetDateTime timestamp;
	private EnumeratedDto user;
	private String changes;
	private EnumeratedDto oldCaseStage;
	private EnumeratedDto newCaseStage;

}
