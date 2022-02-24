package io.zeitwert.ddd.entity.model;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.session.model.SessionInfo;

public interface EntityMeta {

	AppContext getAppContext();

	SessionInfo getSessionInfo();

}
