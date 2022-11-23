
package io.zeitwert.server.session.adapter.rest.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;

@Data()
@Builder
public class SessionInfoReponse {

	private ObjUserDto user;
	private ObjTenantDto tenant;
	private ObjAccountDto account;

	private String locale;
	private String applicationId;
	private List<String> availableApplications;

	public static SessionInfoReponse fromRequest(RequestContext requestCtx, ObjTenant tenant, ObjAccount account) {
		if (requestCtx == null) {
			return null;
		}
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjUserDtoAdapter userDtoAdapter = ObjUserDtoAdapter.getInstance();
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
				.user(userDtoAdapter.fromAggregate(requestCtx.getUser()))
				.tenant(tenantDtoAdapter.fromAggregate(tenant))
				.account(ObjAccountDtoAdapter.getInstance().fromAggregate(account))
				.locale(requestCtx.getLocale().getId())
				.applicationId(defaultApp)
				.availableApplications(List.of())
				.build();
	}

}
