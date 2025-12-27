package io.zeitwert.fm.server.session.service.api.impl;

import io.dddrive.oe.model.ObjUser;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.app.model.impl.RequestContextFMImpl;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.oe.model.enums.CodeLocale;
import io.zeitwert.fm.server.config.security.ZeitwertUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@Profile({"dev", "staging", "prod"})
public class RequestContextProvider {

	@Bean
	@RequestScope
	// cannot use SessionScope, because tenant or account might be switched
	public RequestContextFM getRequestContext(ObjUserFMRepository userRepository) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ("anonymousUser".equals(auth.getPrincipal())) {
			throw new IllegalStateException("Anonymous user is not allowed to access this resource");
		}

		ZeitwertUserDetails userDetails = (ZeitwertUserDetails) auth.getPrincipal();
		ObjUser user = userRepository.get(userDetails.getUserId());
		Integer tenantId = userDetails.getTenantId();
		Integer accountId = userDetails.getAccountId();

		return RequestContextFMImpl.builder()
				.tenantId(tenantId)
				.user(user)
				.accountId(accountId)
				.locale(CodeLocale.DE_CH)
				.build();

	}

}
