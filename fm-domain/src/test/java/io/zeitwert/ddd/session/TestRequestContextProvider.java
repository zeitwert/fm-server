package io.zeitwert.ddd.session;

import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.app.model.impl.RequestContextFMImpl;
import io.zeitwert.fm.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import io.zeitwert.fm.oe.model.enums.CodeLocale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;

@Configuration
@Profile("test")
public class TestRequestContextProvider {

	@Bean
	@SessionScope
	public RequestContextFM getRequestContext(ObjUserRepository userRepository) {
		String userEmail = "tt@zeitwert.io";
		Optional<ObjUser> maybeUser = userRepository.getByEmail(userEmail);
		if (maybeUser.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}
		ObjUser user = maybeUser.get();
		return RequestContextFMImpl.builder()
				.tenantId(user.getTenantId())
				.user(user)
				.accountId(null)
				.locale(CodeLocale.getLocale("en-US"))
				.build();
	}

}
