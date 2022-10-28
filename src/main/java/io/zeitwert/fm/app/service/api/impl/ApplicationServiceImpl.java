
package io.zeitwert.fm.app.service.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.app.model.Application;
import io.zeitwert.ddd.app.model.ApplicationArea;
import io.zeitwert.ddd.app.model.ApplicationInfo;
import io.zeitwert.ddd.app.service.api.ApplicationService;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.session.model.RequestContext;

@Service("applicationService")
@DependsOn("codeUserRoleEnum")
class ApplicationServiceImpl implements ApplicationService {

	private static final ApplicationConfig appConfig = new ApplicationConfig();

	@Autowired
	private RequestContext requestCtx;

	public List<Application> getAllApplications() {
		if (requestCtx.getUser().hasRole(CodeUserRoleEnum.APP_ADMIN)) {
			return appConfig.AppAdminApplications;
		} else if (requestCtx.getUser().hasRole(CodeUserRoleEnum.ADMIN)) {
			return appConfig.AdminApplications;
		}
		return appConfig.UserApplications;
	}

	public Application getApplication(String id) {
		return appConfig.ApplicationMap.get(id);
	}

	public ApplicationInfo getApplicationMenu(String id) {
		return appConfig.ApplicationMenus.get(id);
	}

	public ApplicationArea getArea(String id) {
		return appConfig.Areas.get(id);
	}

}
