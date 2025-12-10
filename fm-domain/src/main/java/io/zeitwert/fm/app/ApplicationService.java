
package io.zeitwert.fm.app;

import java.util.List;

import io.zeitwert.fm.app.model.Application;
import io.zeitwert.fm.app.model.ApplicationArea;
import io.zeitwert.fm.app.model.ApplicationInfo;

public interface ApplicationService {

	List<Application> getAllApplications();

	Application getApplication(String id);

	ApplicationInfo getApplicationMenu(String id);

	ApplicationArea getArea(String id);

}
