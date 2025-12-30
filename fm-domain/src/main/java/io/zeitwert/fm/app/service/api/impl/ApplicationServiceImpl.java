package io.zeitwert.fm.app.service.api.impl;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.fm.app.ApplicationService;
import io.zeitwert.fm.app.model.Application;
import io.zeitwert.fm.app.model.ApplicationArea;
import io.zeitwert.fm.app.model.ApplicationInfo;
import io.zeitwert.fm.oe.model.ObjUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("applicationService")
class ApplicationServiceImpl implements ApplicationService {

	private static final ApplicationConfig appConfig = new ApplicationConfig();

	@Autowired
	private SessionContext requestCtx;

	@Override
	public List<Application> getAllApplications() {
		ObjUser user = (ObjUser) this.requestCtx.getUser();
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
