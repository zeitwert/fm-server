package io.zeitwert.ddd.session;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.dddrive.app.model.RequestContext;
import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.app.service.api.base.AppContextBase;

@Configuration
@Profile({ "test", "ci" })
public class TestAppContextProvider {

	@Bean("appContext")
	public AppContextSPI appContext(
			ApplicationEventPublisher applicationEventPublisher,
			BeanFactory beanFactory,
			RequestContext requestContext) {
		return new TestAppContextImpl(applicationEventPublisher, beanFactory, requestContext);
	}

	/**
	 * Test implementation of AppContext that extends AppContextBase.
	 * Uses the RequestContext bean provided by TestRequestContextProvider.
	 */
	private static class TestAppContextImpl extends AppContextBase {

		private final RequestContext requestContext;

		protected TestAppContextImpl(
				ApplicationEventPublisher applicationEventPublisher,
				BeanFactory beanFactory,
				RequestContext requestContext) {
			super(applicationEventPublisher, beanFactory);
			this.requestContext = requestContext;
		}

		@Override
		public RequestContext getRequestContext() {
			return this.requestContext;
		}

		@Override
		public void beginKernelSession(String userEmail) {
			// No-op for tests - RequestContext is already provided by TestRequestContextProvider
		}

		@Override
		public void endKernelSession() {
			// No-op for tests
		}
	}
}

