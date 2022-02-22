package fm.comunas.ddd.session.service.api.impl;

import fm.comunas.ddd.common.model.enums.CodeLocaleEnum;
import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.oe.model.ObjTenantRepository;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.oe.model.ObjUserRepository;
import fm.comunas.ddd.session.jwt.JwtUtils;
import fm.comunas.ddd.session.model.SessionInfo;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
@Profile({ "dev", "prod" })
public class SessionInfoProvider {

	@Autowired
	private JwtUtils jwtUtils;

	@Bean
	@Autowired
	@SessionScope
	public SessionInfo getSessionInfo(HttpServletRequest request, DSLContext dslContext,
			ObjTenantRepository tenantRepository, ObjUserRepository userRepository) {

		if (request == null) {
			throw new RuntimeException("Authentication error (missing request)");
		} else if (dslContext == null) {
			throw new RuntimeException("Authentication error (missing dslContext)");
		}

		String userEmail;
		if (request.getParameter("isWebhook") != null) {
			userEmail = "martin@comunas.fm";
		} else {
			try {
				String authToken = jwtUtils.getJwtFromHeader(request);
				userEmail = jwtUtils.getUserNameFromJwt(authToken);
			} catch (Exception exception) {
				throw new RuntimeException("Authentication error (corrupt token)");
			}
		}

		if (userEmail == null) {
			throw new RuntimeException("Authentication error (invalid email claim)");
		}

		Optional<ObjUser> user = userRepository.getByEmail(userEmail);
		if (user.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}
		ObjTenant tenant = user.get().getTenant();

		return new SessionInfo(tenant, user.get(), CodeLocaleEnum.getLocale("en-US"));

	}

}
