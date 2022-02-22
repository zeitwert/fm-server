
package fm.comunas.ddd.app.service.api;

import java.util.List;

import fm.comunas.ddd.app.model.Application;
import fm.comunas.ddd.app.model.ApplicationArea;
import fm.comunas.ddd.app.model.ApplicationInfo;

public interface ApplicationService {

	List<Application> getAllApplications();

	Application getApplication(String id);

	ApplicationInfo getApplicationMenu(String id);

	ApplicationArea getArea(String id);

}
