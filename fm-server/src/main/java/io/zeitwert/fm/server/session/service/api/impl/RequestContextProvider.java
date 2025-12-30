package io.zeitwert.fm.server.session.service.api.impl;

import io.zeitwert.fm.app.model.SessionContextFM;
import io.zeitwert.fm.app.model.impl.SessionContextFMImpl;
import io.zeitwert.fm.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import io.zeitwert.fm.oe.model.enums.CodeLocale;
import io.zeitwert.fm.server.config.security.ZeitwertUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
@Profile({"dev", "staging", "prod"})
public class SessionContextProvider {

	@Bean
	@SessionScope
	public SessionContextFM getSessionContext(ObjUserRepository userRepository) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ("anonymousUser".equals(auth.getPrincipal())) {
			throw new IllegalStateException("Anonymous user is not allowed to access this resource");
		}

		ZeitwertUserDetails userDetails = (ZeitwertUserDetails) auth.getPrincipal();
		ObjUser user = userRepository.get(userDetails.getUserId());
		Integer tenantId = userDetails.getTenantId();
		Integer accountId = userDetails.getAccountId();

		return SessionContextFMImpl.builder()
				.tenantId(tenantId)
				.user(user)
				.accountId(accountId)
				.locale(CodeLocale.DE_CH)
				.build();

	}

}
