
package io.zeitwert.ddd.session.adapter.rest.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.session.service.api.SessionService;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;

@Data()
@Builder
public class SessionInfoReponse {

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
		ObjTenantDtoAdapter tenantBridge = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userBridge = ObjUserDtoAdapter.getInstance();
		ObjAccount account = sessionInfo.hasAccount() ? accountRepository.get(sessionInfo, sessionInfo.getAccountId())
				: null;
		ObjAccountDto accountDto = ObjAccountDtoAdapter.getInstance().fromAggregate(account, sessionInfo);
		String defaultApp = sessionInfo.getUser().hasRole(CodeUserRoleEnum.getUserRole("admin")) ? "adminFm" : "userFm";
		// @formatter:off
		return SessionInfoReponse.builder()
			.tenant(tenantBridge.fromAggregate(sessionInfo.getTenant(), sessionInfo))
			.user(userBridge.fromAggregate(sessionInfo.getUser(), sessionInfo))
			.account(accountDto)
			.locale(SessionService.DEFAULT_LOCALE)
			.applicationId(defaultApp)
			.availableApplications(List.of())
			.build();
		// @formatter:on
	}

}
