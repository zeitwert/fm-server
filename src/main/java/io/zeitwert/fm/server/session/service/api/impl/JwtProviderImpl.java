package io.zeitwert.fm.server.session.service.api.impl;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.zeitwert.fm.server.config.security.ZeitwertUserDetails;
import io.zeitwert.fm.server.session.service.api.JwtProvider;

@Service
public class JwtProviderImpl implements JwtProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtProviderImpl.class);

	@Value("${zeitwert.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Override
	public String createJwt(Authentication authentication, Integer tenantId, Integer accountId) {
		ZeitwertUserDetails userPrincipal = (ZeitwertUserDetails) authentication.getPrincipal();
		Map<String, Object> claims = null;
		if (accountId == null) {
			claims = Map.of(TENANT_CLAIM, tenantId);
		} else {
			claims = Map.of(TENANT_CLAIM, tenantId, ACCOUNT_CLAIM, accountId);
		}
		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setExpiration(new Date((new Date()).getTime() + this.jwtExpirationMs))
				.addClaims(claims)
				.signWith(JWT_SECRET_KEY)
				.compact();
	}

	@Override
	public boolean isValidJwt(String authToken) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(JWT_SECRET_KEY)
					.build()
					.parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

}
