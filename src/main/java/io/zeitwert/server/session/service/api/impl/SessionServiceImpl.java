
package io.zeitwert.server.session.service.api.impl;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import static io.zeitwert.ddd.util.Check.assertThis;

import io.zeitwert.ddd.app.event.SessionClosedEvent;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.server.session.service.api.SessionService;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.enums.CodeLocale;
import io.zeitwert.ddd.oe.model.enums.CodeLocaleEnum;
import io.zeitwert.ddd.session.model.SessionInfo;

@Service("sessionService")
class SessionServiceImpl implements SessionService {

	private static final SessionInfo GLOBAL_SESSION = new SessionInfo(null, null, null, null);

	private final AppContext appContext;

	public SessionServiceImpl(AppContext appContext) {
		this.appContext = appContext;
	}

	@Override
	public SessionInfo getGlobalSession() {
		return GLOBAL_SESSION;
	}

	@Override
	public SessionInfo openSession(ObjUser user) {
		return openSession(user, CodeLocaleEnum.getLocale(DEFAULT_LOCALE));
	}

	@Override
	public SessionInfo openSession(ObjUser user, CodeLocale locale) {
		assertThis(user != null, "valid user");
		return new SessionInfo(user.getTenant(), user, null, locale);
	}

	@Override
	public void closeSession(SessionInfo sessionInfo) {
		ApplicationEvent sessionClosedEvent = new SessionClosedEvent(this, sessionInfo);
		this.appContext.publishApplicationEvent(sessionClosedEvent);
	}

}
