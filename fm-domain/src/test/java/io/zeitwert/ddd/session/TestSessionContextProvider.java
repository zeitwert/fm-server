package io.zeitwert.ddd.session;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.app.model.SessionContextFM;
import io.zeitwert.fm.app.model.impl.SessionContextFMImpl;
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
public class TestSessionContextProvider {

	static final String userEmail = "tt@zeitwert.io";
	static final String accountKey = "Testlingen";

	@Bean
	@SessionScope
	public SessionContextFM getRequestContext(ObjUserRepository userRepository, ObjAccountRepository accountRepository) {
		Optional<ObjUser> maybeUser = userRepository.getByEmail(userEmail);
		if (maybeUser.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown user " + userEmail + ")");
		}
		ObjUser user = maybeUser.get();
		Optional<ObjAccount> maybeAccount = accountRepository.getByKey(accountKey);
		if (maybeAccount.isEmpty()) {
			throw new RuntimeException("Authentication error (unknown account " + accountKey + ")");
		}
		ObjAccount account = maybeAccount.get();
		return SessionContextFMImpl.builder()
				.tenantId(user.getTenantId())
				.user(user)
				.accountId((Integer) account.getId())
				.locale(CodeLocale.getLocale("en-US"))
				.build();
	}

}
