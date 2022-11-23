package io.zeitwert.fm.app.adapter.api.rest.dto;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TenantInfoResponse {

	private Integer id;
	private EnumeratedDto tenantType;
	private List<EnumeratedDto> accounts;

}
