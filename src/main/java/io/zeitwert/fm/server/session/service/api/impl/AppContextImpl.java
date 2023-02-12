package io.zeitwert.fm.server.session.service.api.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.dddrive.app.model.RequestContext;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.app.service.api.base.AppContextBase;
import io.dddrive.app.service.api.impl.Enumerations;

@Service("appContext")
@DependsOn("kernelBootstrap")
public class AppContextImpl extends AppContextBase implements AppContext, AppContextSPI {

	protected AppContextImpl(
			ApplicationContext applicationContext,
			ApplicationEventPublisher applicationEventPublisher,
			Enumerations enums,
			RequestContext requestContext) {
		super(applicationContext, applicationEventPublisher, enums, requestContext);
	}

}
