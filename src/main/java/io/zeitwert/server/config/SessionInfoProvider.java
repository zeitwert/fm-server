package io.zeitwert.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.server.config.security.ZeitwertUserDetails;

@Configuration
@Profile({ "dev", "staging", "prod" })
public class SessionInfoProvider {

	static final String DEFAULT_LOCALE = "de-CH";

	@Bean
	@Autowired
	@RequestScope
	// cannot use SessionScope, because tenant or account might be switched
	public RequestContext getSessionInfo(ObjUserRepository userRepo) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ZeitwertUserDetails userDetails = (ZeitwertUserDetails) auth.getPrincipal();
		ObjUser user = userRepo.get(null, userDetails.getUserId());
		return new RequestContext(user, userDetails.getAccountId(), CodeLocaleEnum.getLocale(DEFAULT_LOCALE));

	}

}
