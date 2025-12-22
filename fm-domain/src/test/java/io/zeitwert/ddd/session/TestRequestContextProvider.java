package io.zeitwert.ddd.session;

import io.dddrive.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.oe.model.enums.CodeLocale;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.app.model.impl.RequestContextFMImpl;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
@Profile("test")
public class TestRequestContextProvider {

	@Bean
	@SessionScope
	public RequestContextFM getRequestContext(ObjUserFMRepository userRepository) {
		String userEmail = "tt@zeitwert.io";
		Optional<ObjUserFM> maybeUser = userRepository.getByEmail(userEmail);
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
