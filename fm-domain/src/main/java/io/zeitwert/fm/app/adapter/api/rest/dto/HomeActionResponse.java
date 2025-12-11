
package io.zeitwert.fm.app.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;

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
