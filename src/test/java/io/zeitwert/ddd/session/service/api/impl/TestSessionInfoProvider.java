package io.zeitwert.ddd.session.service.api.impl;

import io.zeitwert.ddd.common.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.jooq.DSLContext;
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
	public SessionInfo getSessionInfo(HttpServletRequest request, DSLContext dslContext,
			ObjTenantRepository tenantRepository, ObjUserRepository userRepository) {

		String userEmail = "martin@comunas.fm";
		Optional<ObjUser> user = userRepository.getByEmail(userEmail);
		if (user.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}
		ObjTenant tenant = user.get().getTenant();

		return new SessionInfo(tenant, user.get(), CodeLocaleEnum.getLocale("en-US"));

	}

}
