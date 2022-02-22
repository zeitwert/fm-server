
package fm.comunas.ddd.app.service.api.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fm.comunas.ddd.app.model.Application;
import fm.comunas.ddd.app.model.ApplicationArea;
import fm.comunas.ddd.app.model.ApplicationInfo;
import fm.comunas.ddd.app.service.api.ApplicationService;

@Service("applicationService")
class ApplicationServiceImpl implements ApplicationService {

	private static final ApplicationConfig appConfig = new ApplicationConfig();

	public List<Application> getAllApplications() {
		return appConfig.Applications;
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
