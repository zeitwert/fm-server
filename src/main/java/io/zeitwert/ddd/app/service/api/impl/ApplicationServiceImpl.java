
package io.zeitwert.ddd.app.service.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.app.model.Application;
import io.zeitwert.ddd.app.model.ApplicationArea;
import io.zeitwert.ddd.app.model.ApplicationInfo;
import io.zeitwert.ddd.app.service.api.ApplicationService;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.session.model.SessionInfo;

@Service("applicationService")
@DependsOn("codeUserRoleEnum")
class ApplicationServiceImpl implements ApplicationService {

	private static final ApplicationConfig appConfig = new ApplicationConfig();

	private static final CodeUserRole AdminRole = CodeUserRoleEnum.getUserRole("admin");
	private static final CodeUserRole UserRole = CodeUserRoleEnum.getUserRole("user");

	@Autowired
	private SessionInfo sessionInfo;

	public List<Application> getAllApplications() {
		if (sessionInfo.getUser().hasRole(AdminRole)) {
			return appConfig.AdminApplications;
		} else if (sessionInfo.getUser().hasRole(UserRole)) {
			return appConfig.UserApplications;
		}
		return List.of();
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
