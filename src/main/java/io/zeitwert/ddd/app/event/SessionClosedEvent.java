package io.zeitwert.ddd.app.event;

import io.zeitwert.ddd.session.model.RequestContext;

import org.springframework.context.ApplicationEvent;

public class SessionClosedEvent extends ApplicationEvent {

	private RequestContext requestCtx;

	public SessionClosedEvent(Object source, RequestContext requestCtx) {
		super(source);
		this.requestCtx = requestCtx;
	}

	public RequestContext getSessionInfo() {
		return this.requestCtx;
	}

}
