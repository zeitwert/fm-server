package io.zeitwert.ddd.session.service.api;

import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

public interface JwtProvider {

	static final String TENANT_CLAIM = "zw/tenant";
	static final String ACCOUNT_CLAIM = "zw/accountId";

	String createJwt(Authentication authentication, Integer accountId);

	String getJwtFromHeader(HttpServletRequest request);

	boolean isValidJwt(String authToken);

	Claims getClaims(String token);

}
