
package io.zeitwert.server.session.adapter.rest.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.session.model.RequestContext;
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

	public static SessionInfoReponse fromRequest(RequestContext requestCtx, ObjAccountRepository accountRepository) {
		if (requestCtx == null) {
			return null;
		}
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
		// @formatter:off
		ObjAccount account = requestCtx.hasAccount() ? accountRepository.get(requestCtx, requestCtx.getAccountId()) : null;
		ObjAccountDto accountDto = ObjAccountDtoAdapter.getInstance().fromAggregate(account, requestCtx);
		ObjUser user = requestCtx.getUser();
		String defaultApp = null;
		if (user.hasRole(CodeUserRoleEnum.APP_ADMIN)) {
			defaultApp = "appAdmin";
		} else if (user.hasRole(CodeUserRoleEnum.ADMIN)) {
			defaultApp = "tenantAdmin";
		} else {
			defaultApp = "fm";
		}
		return SessionInfoReponse.builder()
			.tenant(tenantDtoAdapter.fromAggregate(requestCtx.getTenant(), requestCtx))
			.user(userDtoAdapter.fromAggregate(requestCtx.getUser(), requestCtx))
			.account(accountDto)
			.locale(requestCtx.getLocale().getId())
			.applicationId(defaultApp)
			.availableApplications(List.of())
			.build();
		// @formatter:on
	}

}
