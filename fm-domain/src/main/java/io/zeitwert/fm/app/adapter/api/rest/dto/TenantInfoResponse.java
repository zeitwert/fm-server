package io.zeitwert.fm.app.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;

@Data
@Builder
public class TenantInfoResponse {

	private Integer id;
	private EnumeratedDto tenantType;
	private List<EnumeratedDto> accounts;

}
