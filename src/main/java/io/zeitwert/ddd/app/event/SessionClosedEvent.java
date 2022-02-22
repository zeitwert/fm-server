package io.zeitwert.ddd.app.event;

import io.zeitwert.ddd.session.model.SessionInfo;

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
