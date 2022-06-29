package io.zeitwert.fm.building.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data()
@Builder
public class NoteTransferDto {

	private String subject;
	private String content;
	private Boolean isPrivate;

	private String createdByUser;
	private OffsetDateTime createdAt;

	private String modifiedByUser;
	private OffsetDateTime modifiedAt;

}
