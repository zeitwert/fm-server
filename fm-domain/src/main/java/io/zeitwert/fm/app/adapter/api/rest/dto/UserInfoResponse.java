package io.zeitwert.fm.app.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;

@Data
@Builder
public class UserInfoResponse {

	private Integer id;
	private String name;
	private String email;

	private EnumeratedDto role;
	private List<EnumeratedDto> tenants;

}
