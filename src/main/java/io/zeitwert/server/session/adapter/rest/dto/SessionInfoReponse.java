
package io.zeitwert.server.session.adapter.rest.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountLoginDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountLoginDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter;
import io.zeitwert.server.session.version.ApplicationInfo;

@Data()
@Builder
public class SessionInfoReponse {

	private ObjUserDto user;
	private ObjTenantDto tenant;
	private ObjAccountLoginDto account;

	private String locale;
	private String applicationId;
	private String applicationName;
	private String applicationVersion;
	private List<String> availableApplications;

	public static SessionInfoReponse fromRequest(RequestContext requestCtx, ObjTenant tenant, ObjAccount account) {
		if (requestCtx == null) {
			return null;
		}
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		ObjAccountLoginDtoAdapter accountDtoAdapter = ObjAccountLoginDtoAdapter.getInstance();
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
				.applicationName(ApplicationInfo.getName())
				.applicationVersion(ApplicationInfo.getVersion())
				.user(userDtoAdapter.fromAggregate(requestCtx.getUser()))
				.tenant(tenantDtoAdapter.fromAggregate(tenant))
				.account(accountDtoAdapter.fromAggregate(account))
				.locale(requestCtx.getLocale().getId())
				.applicationId(defaultApp)
				.availableApplications(List.of())
				.build();
	}

}
