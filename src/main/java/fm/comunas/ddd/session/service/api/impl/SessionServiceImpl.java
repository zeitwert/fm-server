
package fm.comunas.ddd.session.service.api.impl;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import fm.comunas.ddd.app.event.SessionClosedEvent;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.common.model.enums.CodeLocale;
import fm.comunas.ddd.common.model.enums.CodeLocaleEnum;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.ddd.session.service.api.SessionService;

@Service("sessionService")
class SessionServiceImpl implements SessionService {

	private static final SessionInfo GLOBAL_SESSION = new SessionInfo(null, null, null);

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
		return openSession(user, CodeLocaleEnum.getLocale("en-US"));
	}

	@Override
	public SessionInfo openSession(ObjUser user, CodeLocale locale) {
		Assert.isTrue(user != null, "valid user");
		return new SessionInfo(user.getTenant(), user, locale);
	}

	@Override
	public void closeSession(SessionInfo sessionInfo) {
		ApplicationEvent sessionClosedEvent = new SessionClosedEvent(this, sessionInfo);
		this.appContext.publishApplicationEvent(sessionClosedEvent);
	}

}
