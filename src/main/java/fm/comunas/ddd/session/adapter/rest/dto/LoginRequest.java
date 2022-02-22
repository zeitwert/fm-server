package fm.comunas.ddd.session.adapter.rest.dto;

import lombok.Data;

@Data
public class LoginRequest {

	private String email;
	private String password;

}
