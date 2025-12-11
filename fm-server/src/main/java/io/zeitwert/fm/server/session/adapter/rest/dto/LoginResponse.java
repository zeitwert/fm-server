package io.zeitwert.fm.server.session.adapter.rest.dto;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

	private final String tokenType = "Bearer";
	private String token;

	private Integer id;
	private String username;
	private String email;
	private Integer accountId;
	private EnumeratedDto role;

}
