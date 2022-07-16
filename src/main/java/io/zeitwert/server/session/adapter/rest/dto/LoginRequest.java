package io.zeitwert.server.session.adapter.rest.dto;

import lombok.Data;

@Data
public class LoginRequest {

	private String email;
	private String password;
	private Integer accountId;

}
