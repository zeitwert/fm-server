package io.zeitwert.ddd.session.adapter.rest.dto;

import lombok.Data;

@Data
public class LoginRequest {

	private String email;
	private String password;

}
