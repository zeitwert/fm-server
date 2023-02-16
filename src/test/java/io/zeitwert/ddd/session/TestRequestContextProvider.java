package io.zeitwert.ddd.session;

import io.dddrive.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.enums.CodeLocaleEnum;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.app.model.impl.RequestContextFMImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
@Profile("test")
public class TestRequestContextProvider {

	@Bean
	@Autowired
	@SessionScope
	public RequestContextFM getRequestContext(ObjUserCache userCache) {

		String userEmail = "tt@zeitwert.io";
		Optional<ObjUser> maybeUser = userCache.getByEmail(userEmail);
		if (maybeUser.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}

		ObjUser user = maybeUser.get();
		return RequestContextFMImpl.builder()
				.tenantId(user.getTenantId())
				.user(user)
				.accountId(null)
				.locale(CodeLocaleEnum.getLocale("en-US"))
				.build();
	}

}
