package io.zeitwert.ddd.session.service.api;

import org.springframework.security.core.Authentication;

public interface JwtProvider {

	String getJwtToken(Authentication authentication);

}
