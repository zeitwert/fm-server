package fm.comunas.server.session.jwt;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import fm.comunas.ddd.session.model.impl.UserDetailsImpl;
import fm.comunas.ddd.session.service.api.JwtProvider;
import io.jsonwebtoken.*;

@Service
public class JwtProviderImpl implements JwtProvider {

	@Value("${comunas.app.jwtSecret}")
	private String jwtSecret;

	@Value("${comunas.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String getJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		//@formatter:off
		return Jwts.builder()
			.setSubject((userPrincipal.getUsername()))
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
			.addClaims(
				Map.of(
					"https://comunas.fm/email", userPrincipal.getEmail(),
					"https://comunas.fm/tenant", userPrincipal.getTenant()
				)
			)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact();
		//@formatter:on
	}

}
