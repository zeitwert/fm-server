package fm.comunas.ddd.app.event;

import fm.comunas.ddd.session.model.SessionInfo;

import org.springframework.context.ApplicationEvent;

public class SessionClosedEvent extends ApplicationEvent {

	private SessionInfo sessionInfo;

	public SessionClosedEvent(Object source, SessionInfo sessionInfo) {
		super(source);
		this.sessionInfo = sessionInfo;
	}

	public SessionInfo getSessionInfo() {
		return this.sessionInfo;
	}

}
