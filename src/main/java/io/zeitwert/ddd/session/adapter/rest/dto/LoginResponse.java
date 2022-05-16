package io.zeitwert.ddd.session.adapter.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {

	private final String tokenType = "Bearer";
	private String token;

	private Integer id;
	private String username;
	private String email;
	private Integer accountId;
	private List<String> roles;

}
