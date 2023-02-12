package io.zeitwert.fm.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

import io.dddrive.app.model.RequestContext;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.model.enums.CodeLocaleEnum;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.server.config.security.ZeitwertUserDetails;

@Configuration
@Profile({ "dev", "staging", "prod" })
public class RequestContextProvider {

	static final String DEFAULT_LOCALE = "de-CH";

	@Bean
	@Autowired
	@RequestScope
	// cannot use SessionScope, because tenant or account might be switched
	public RequestContext getRequestContext(ObjUserCache userCache) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ("anonymousUser".equals(auth.getPrincipal())) {
			throw new IllegalStateException("Anonymous user is not allowed to access this resource");
		}

		ZeitwertUserDetails userDetails = (ZeitwertUserDetails) auth.getPrincipal();
		ObjUser user = userCache.get(userDetails.getUserId());
		Integer tenantId = userDetails.getTenantId();
		Integer accountId = userDetails.getAccountId();

		return new RequestContext(user, tenantId, accountId, CodeLocaleEnum.getLocale(DEFAULT_LOCALE));

	}

}
