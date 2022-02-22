package fm.comunas.ddd.entity.model;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.session.model.SessionInfo;

public interface EntityMeta {

	AppContext getAppContext();

	SessionInfo getSessionInfo();

}
