package fm.comunas.ddd.session.service.api;

import org.springframework.security.core.Authentication;

public interface JwtProvider {

	String getJwtToken(Authentication authentication);

}
