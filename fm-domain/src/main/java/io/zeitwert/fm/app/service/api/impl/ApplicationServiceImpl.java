
package io.zeitwert.fm.app.service.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.dddrive.app.model.RequestContext;
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

	@Override
	public List<Application> getAllApplications() {
		ObjUserFM user = (ObjUserFM) this.requestCtx.getUser();
		if (user.isAppAdmin()) {
			return appConfig.AppAdminApplications;
		} else if (user.isAdmin()) {
			return appConfig.AdminApplications;
		}
		return appConfig.UserApplications;
	}

	@Override
	public Application getApplication(String id) {
		return appConfig.ApplicationMap.get(id);
	}

	@Override
	public ApplicationInfo getApplicationMenu(String id) {
		return appConfig.ApplicationMenus.get(id);
	}

	@Override
	public ApplicationArea getArea(String id) {
		return appConfig.Areas.get(id);
	}

}
