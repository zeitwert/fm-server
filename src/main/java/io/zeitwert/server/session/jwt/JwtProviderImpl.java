package io.zeitwert.server.session.jwt;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.zeitwert.ddd.session.model.impl.UserDetailsImpl;
import io.zeitwert.ddd.session.service.api.JwtProvider;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtProviderImpl implements JwtProvider {

	private static final String AUTH_HEADER_PREFIX = "Bearer ";
	private static final Logger logger = LoggerFactory.getLogger(JwtProviderImpl.class);

	@Value("${zeitwert.app.jwtSecret}")
	private String jwtSecret;

	@Value("${zeitwert.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String getJwt(Authentication authentication, Integer accountId) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		//@formatter:off
		return Jwts.builder()
			.setSubject((userPrincipal.getUsername()))
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
			.addClaims(
				Map.of(
					TENANT_CLAIM, userPrincipal.getTenant(),
					ACCOUNT_CLAIM, accountId
				)
			)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact();
		//@formatter:on
	}

	public String getJwtFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !StringUtils.hasText(authHeader)) {
			return null;
		} else if (authHeader.startsWith(AUTH_HEADER_PREFIX)) {
			return authHeader.substring(AUTH_HEADER_PREFIX.length());
		}
		throw new RuntimeException("Authentication error (missing / invalid JWT)");
	}

	public boolean isValidJwt(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
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

	public Claims getClaims(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
	}

}
