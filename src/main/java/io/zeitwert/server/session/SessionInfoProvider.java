package io.zeitwert.server.session;

import io.jsonwebtoken.Claims;
import io.zeitwert.ddd.common.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.oe.model.ObjTenant;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@Profile({ "dev", "prod" })
public class SessionInfoProvider {

	@Autowired
	private JwtProvider jwtProvider;

	@Bean
	@Autowired
	@RequestScope
	public SessionInfo getSessionInfo(HttpServletRequest request, DSLContext dslContext,
			ObjTenantRepository tenantRepository, ObjUserRepository userRepository, ObjAccountRepository accountRepository) {

		if (request == null) {
			throw new RuntimeException("Authentication error (missing request)");
		} else if (dslContext == null) {
			throw new RuntimeException("Authentication error (missing dslContext)");
		}

		String authToken = jwtProvider.getJwtFromHeader(request);
		Claims claims = jwtProvider.getClaims(authToken);

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
		ObjTenant tenant = user.get().getTenant();

		Integer accountId;
		try {
			accountId = (Integer) claims.get(JwtProvider.ACCOUNT_CLAIM);
		} catch (Exception exception) {
			throw new RuntimeException("Authentication error (corrupt token, accountId)");
		}

		return new SessionInfo(tenant, user.get(), accountId, CodeLocaleEnum.getLocale("en-US"));

	}

}
