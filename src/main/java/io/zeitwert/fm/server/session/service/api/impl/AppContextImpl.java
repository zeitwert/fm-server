package io.zeitwert.fm.server.session.service.api.impl;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import io.dddrive.app.model.RequestContext;
import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.app.service.api.base.AppContextBase;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.app.model.impl.RequestContextFMImpl;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;
import io.zeitwert.fm.oe.model.enums.CodeLocaleEnum;

@Service("appContext")
public class AppContextImpl extends AppContextBase implements AppContextSPI {

	private static final String K_ZEITWERT_IO = "k@zeitwert.io";

	private RequestContext kernelRequestContext;

	protected AppContextImpl(ApplicationEventPublisher applicationEventPublisher, BeanFactory beanFactory) {
		super(applicationEventPublisher, beanFactory);
	}

	@Override
	public RequestContext getRequestContext() {
		if (this.kernelRequestContext != null) {
			return this.kernelRequestContext;
		}
		return super.getRequestContext();
	}

	@Override
	public void beginKernelSession() {
		ObjUser kernelUser = ((ObjUserCache) this.getCache(ObjUser.class)).getByEmail(K_ZEITWERT_IO).get();
		this.kernelRequestContext = RequestContextFMImpl.builder()
				.tenantId(ObjTenantFMRepository.KERNEL_TENANT_ID)
				.user(kernelUser)
				.locale(CodeLocaleEnum.getLocale("en-US"))
				.accountId(null)
				.build();
	}

	@Override
	public void endKernelSession() {
		this.kernelRequestContext = null;
	}

}
