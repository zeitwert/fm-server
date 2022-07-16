package io.zeitwert.server.session.service.api;

import org.springframework.security.core.Authentication;

public interface JwtProvider {

	final String TENANT_CLAIM = "zw/tenant";
	final String ACCOUNT_CLAIM = "zw/accountId";

	String createJwt(Authentication authentication, Integer accountId);

	boolean isValidJwt(String authToken);

}
