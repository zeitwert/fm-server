
package io.zeitwert.fm.app.service.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.app.ApplicationService;
import io.zeitwert.fm.app.model.Application;
import io.zeitwert.fm.app.model.ApplicationArea;
import io.zeitwert.fm.app.model.ApplicationInfo;
import io.zeitwert.fm.oe.model.ObjUserFM;

@Service("applicationService")
@DependsOn("codeUserRoleEnum")
class ApplicationServiceImpl implements ApplicationService {

	private static final ApplicationConfig appConfig = new ApplicationConfig();

	@Autowired
	private RequestContext requestCtx;

	public List<Application> getAllApplications() {
		ObjUserFM user = (ObjUserFM) requestCtx.getUser();
		if (user.hasRole(CodeUserRoleEnum.APP_ADMIN)) {
			return appConfig.AppAdminApplications;
		} else if (user.hasRole(CodeUserRoleEnum.ADMIN)) {
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
