package io.zeitwert.fm.building.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data()
@Builder
public class TransferMetaDto {

	private String aggregate;
	private String version;

	private String createdByUser;
	private OffsetDateTime createdAt;

	private String modifiedByUser;
	private OffsetDateTime modifiedAt;

}
