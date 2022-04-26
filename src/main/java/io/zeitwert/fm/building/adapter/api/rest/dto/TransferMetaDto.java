package io.zeitwert.fm.building.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class TransferMetaDto {

	private String aggregate;
	private String version;

}
