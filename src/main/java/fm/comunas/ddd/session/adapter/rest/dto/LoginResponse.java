package fm.comunas.ddd.session.adapter.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class LoginResponse {

	private String token;
	private final String type = "Bearer";
	private Integer id;
	private String username;
	private String email;
	private List<String> roles;
	private Map<String, Object> customValues;

}
