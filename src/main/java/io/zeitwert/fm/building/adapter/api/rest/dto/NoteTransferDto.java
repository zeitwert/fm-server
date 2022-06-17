package io.zeitwert.fm.building.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class NoteTransferDto {

	private String subject;
	private String content;
	private Boolean isPrivate;

}
