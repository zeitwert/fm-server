package io.zeitwert.fm.server.session.service.api;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Jwts;

public interface JwtProvider {

	final String AUTH_HEADER_PREFIX = "Bearer ";
	final SecretKey JWT_SECRET_KEY = Jwts.SIG.HS512.key().build();

	final String TENANT_CLAIM = "zw/tenantId";
	final String ACCOUNT_CLAIM = "zw/accountId";

	String createJwt(Authentication authentication, Integer tenantId, Integer accountId);

	boolean isValidJwt(String authToken);

}
