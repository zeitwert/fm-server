package io.zeitwert.ddd.session;

import io.dddrive.app.model.RequestContext;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.model.enums.CodeLocaleEnum;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

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
	public RequestContext getRequestContext(ObjUserFMRepository userRepository) {
		String userEmail = "tt@zeitwert.io";
		Optional<ObjUserFM> maybeUser = userRepository.getByEmail(userEmail);
		if (maybeUser.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}
		ObjUser user = maybeUser.get();
		return new RequestContext(user, user.getTenantId(), null, CodeLocaleEnum.getLocale("en-US"));
	}

}
