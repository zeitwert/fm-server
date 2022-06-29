package io.zeitwert.ddd.session.service.api.impl;

import io.zeitwert.fm.account.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
@Profile("test")
public class TestSessionInfoProvider {

	@Bean
	@Autowired
	@SessionScope
	public SessionInfo getSessionInfo(ObjUserRepository userRepository) {

		String userEmail = "k@zeitwert.io";
		Optional<ObjUser> user = userRepository.getByEmail(userEmail);
		if (user.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}
		ObjTenant tenant = user.get().getTenant();

		return new SessionInfo(tenant, user.get(), null, CodeLocaleEnum.getLocale("en-US"));

	}

	public static SessionInfo getOtherSession(ObjUserRepository userRepository) {
		String userEmail = "hannes@zeitwert.io";
		Optional<ObjUser> user = userRepository.getByEmail(userEmail);
		if (user.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}
		ObjTenant tenant = user.get().getTenant();
		return new SessionInfo(tenant, user.get(), null, CodeLocaleEnum.getLocale("en-US"));
	}

}
