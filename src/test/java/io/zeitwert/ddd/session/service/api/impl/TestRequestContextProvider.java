package io.zeitwert.ddd.session.service.api.impl;

import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.session.model.RequestContext;

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
	public RequestContext getRequestContext(ObjUserRepository userRepository) {
		String userEmail = "k@zeitwert.io";
		Optional<ObjUser> user = userRepository.getByEmail(userEmail);
		if (user.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}
		return new RequestContext(user.get(), null, CodeLocaleEnum.getLocale("en-US"));
	}

}
