package fm.comunas.ddd.session.service.api.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionCookieFilter extends GenericFilterBean {

	private final String SESSION_COOKIE_NAME = "JSESSIONID";
	private final String SID_ATTRIBUTES = ";Path=/;HttpOnly;Secure;SameSite=None";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String requestUrl = req.getRequestURL().toString();
		boolean isRelevant = requestUrl.contains("/api/app/userInfo") || requestUrl.contains("/api/session");
		if (isRelevant) {
			String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
			resp.setHeader(HttpHeaders.SET_COOKIE, SESSION_COOKIE_NAME + "=" + sessionId + SID_ATTRIBUTES);
		}
		chain.doFilter(request, response);
	}

}