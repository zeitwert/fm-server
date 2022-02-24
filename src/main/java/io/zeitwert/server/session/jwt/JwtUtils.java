package io.zeitwert.server.session.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.*;

import javax.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

	private static final String AUTH_HEADER_PREFIX = "Bearer ";
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${zeitwert.app.jwtSecret}")
	private String jwtSecret;

	@Value("${zeitwert.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String getJwtFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !StringUtils.hasText(authHeader)) {
			return null;
		} else if (authHeader.startsWith(AUTH_HEADER_PREFIX)) {
			return authHeader.substring(AUTH_HEADER_PREFIX.length());
		}
		throw new RuntimeException("Authentication error (missing / invalid JWT)");
	}

	public boolean validateJwt(String authToken) {
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

	public String getUserNameFromJwt(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

}
