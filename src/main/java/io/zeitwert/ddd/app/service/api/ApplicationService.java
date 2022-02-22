
package io.zeitwert.ddd.app.service.api;

import java.util.List;

import io.zeitwert.ddd.app.model.Application;
import io.zeitwert.ddd.app.model.ApplicationArea;
import io.zeitwert.ddd.app.model.ApplicationInfo;

public interface ApplicationService {

	List<Application> getAllApplications();

	Application getApplication(String id);

	ApplicationInfo getApplicationMenu(String id);

	ApplicationArea getArea(String id);

}
