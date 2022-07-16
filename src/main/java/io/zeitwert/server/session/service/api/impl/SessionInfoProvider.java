package io.zeitwert.server.session.service.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeLocale;
import io.zeitwert.ddd.oe.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.server.session.service.api.SessionService;

@Configuration
@Profile({ "dev", "staging", "prod" })
public class SessionInfoProvider {

	@Bean
	@Autowired
	@RequestScope
	// cannot use SessionScope, because tenant or account might be switched
	public SessionInfo getSessionInfo(ObjUserRepository userRepo) {

		CodeLocale DEFAULT_LOCALE = CodeLocaleEnum.getLocale(SessionService.DEFAULT_LOCALE);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ZeitwertUserDetails userDetails = (ZeitwertUserDetails) auth.getPrincipal();
		ObjUser user = userRepo.get(userDetails.getUserId());
		return new SessionInfo(user.getTenant(), user, userDetails.getAccountId(), DEFAULT_LOCALE);

	}

}
