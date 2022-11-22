package io.zeitwert.fm.app.adapter.api.rest.dto;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoResponse {

	private Integer id;
	private String name;
	private String email;
	private ObjTenantDto tenant;
	private EnumeratedDto role;
	private List<EnumeratedDto> accounts;

}
