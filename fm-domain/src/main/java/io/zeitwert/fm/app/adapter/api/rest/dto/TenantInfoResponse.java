package io.zeitwert.fm.app.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;

@Data
@Builder
public class TenantInfoResponse {

	private Integer id;
	private EnumeratedDto tenantType;
	private List<EnumeratedDto> accounts;

}
