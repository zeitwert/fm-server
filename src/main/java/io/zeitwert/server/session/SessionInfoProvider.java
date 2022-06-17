package io.zeitwert.server.session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.zeitwert.fm.account.model.enums.CodeLocale;
import io.zeitwert.fm.account.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.session.service.api.JwtProvider;
import io.zeitwert.fm.account.model.ObjAccountRepository;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;

import static io.zeitwert.ddd.util.Check.requireThis;

@Configuration
@Profile({ "dev", "prod" })
public class SessionInfoProvider {

	public static final String AUTH_HEADER_PREFIX = "Bearer ";

	@Value("${zeitwert.app.jwtSecret}")
	private String jwtSecret;

	@Bean
	@Autowired
	@RequestScope // cannot do SessionScope, because tenantId or accountId might be switched
	public SessionInfo getSessionInfo(HttpServletRequest request, DSLContext dslContext,
			ObjTenantRepository tenantRepository, ObjUserRepository userRepository, ObjAccountRepository accountRepository) {

		CodeLocale EN_US = CodeLocaleEnum.getLocale("en-US");

		if (request == null) {
			throw new RuntimeException("Authentication error (missing request)");
		} else if (dslContext == null) {
			throw new RuntimeException("Authentication error (missing dslContext)");
		}

		if (!hasJwt(request)) {
			// localhost dev login
			if (request.getServerName().equals("localhost")) {
				Optional<ObjUser> user = userRepository.getByEmail("hannes@zeitwert.io");
				return new SessionInfo(user.get().getTenant(), user.get(), null, EN_US);
			}
			return SessionInfo.NO_SESSION;
		}

		String authToken;
		Claims claims;
		try {
			authToken = this.getJwtFromHeader(request);
			claims = this.getClaims(authToken);
		} catch (Exception exception) {
			throw new RuntimeException("Authentication error (corrupt or missing JWT)");
		}

		String userEmail;
		try {
			userEmail = claims.getSubject();
		} catch (Exception exception) {
			throw new RuntimeException("Authentication error (corrupt token, email)");
		}
		if (userEmail == null) {
			throw new RuntimeException("Authentication error (invalid email claim)");
		}

		Optional<ObjUser> user = userRepository.getByEmail(userEmail);
		if (user.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}

		Integer accountId;
		try {
			accountId = (Integer) claims.get(JwtProvider.ACCOUNT_CLAIM);
		} catch (Exception exception) {
			throw new RuntimeException("Authentication error (corrupt token, missing accountId)");
		}

		return new SessionInfo(user.get().getTenant(), user.get(), accountId, EN_US);

	}

	private boolean hasJwt(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		return StringUtils.hasText(authHeader) && authHeader.startsWith(AUTH_HEADER_PREFIX);
	}

	private String getJwtFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (!StringUtils.hasText(authHeader)) {
			return null;
		}
		if (authHeader.startsWith(AUTH_HEADER_PREFIX)) {
			return authHeader.substring(AUTH_HEADER_PREFIX.length());
		}
		throw new RuntimeException("Authentication error (missing / invalid JWT)");
	}

	private Claims getClaims(String token) {
		requireThis(token != null && token.length() > 0, "valid JWT (" + token + ")");
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
	}

}
