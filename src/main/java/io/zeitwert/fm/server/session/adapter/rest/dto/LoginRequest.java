package io.zeitwert.fm.server.session.adapter.rest.dto;

import lombok.Data;

@Data
public class LoginRequest {

	private String email;
	private String password;
	private Integer tenantId;
	private Integer accountId;

}
