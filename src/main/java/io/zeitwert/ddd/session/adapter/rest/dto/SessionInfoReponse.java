
package io.zeitwert.ddd.session.adapter.rest.dto;

import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoBridge;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;

@Data()
@Builder
public class SessionInfoReponse {

	private static final List<String> apps = Arrays.asList("fm", "admin");

	private ObjTenantDto tenant;

	private ObjUserDto user;

	private ObjAccountDto account;

	private String locale;

	private String applicationId;

	private List<String> availableApplications;

	public static SessionInfoReponse fromSession(SessionInfo sessionInfo, ObjAccountRepository accountRepository) {
		if (sessionInfo == null) {
			return null;
		}
		ObjAccount account = sessionInfo.hasAccount() ? accountRepository.get(sessionInfo, sessionInfo.getAccountId())
				: null;
		ObjAccountDto accountDto = ObjAccountDtoBridge.getInstance().fromAggregate(account, sessionInfo);
		// @formatter:off
		return SessionInfoReponse.builder()
			.tenant(ObjTenantDto.fromObj(sessionInfo.getTenant()))
			.user(ObjUserDto.fromObj(sessionInfo.getUser()))
			.account(accountDto)
			.locale("en-US")
			.applicationId("fm")
			.availableApplications(apps)
			.build();
		// @formatter:on
	}

}
