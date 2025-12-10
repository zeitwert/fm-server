package io.zeitwert.fm.server.config.crnk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;

import io.crnk.core.engine.http.HttpRequestContext;
import io.crnk.core.engine.http.DefaultHttpRequestContextBase;
import io.crnk.core.engine.http.HttpResponse;
import io.crnk.core.engine.query.QueryContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Jakarta Servlet API compatible HttpRequestContext implementation for Crnk.
 * 
 * This bridges jakarta.servlet (Spring Boot 3) to crnk's internal HTTP abstraction.
 */
public class JakartaHttpRequestContext extends DefaultHttpRequestContextBase implements HttpRequestContext {
	
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final String pathPrefix;
	private HttpResponse httpResponse;
	private QueryContext queryContext;
	private byte[] requestBody;
	private final Map<String, Object> requestAttributes = new HashMap<>();
	
	public JakartaHttpRequestContext(HttpServletRequest request, HttpServletResponse response, String pathPrefix) {
		this.request = request;
		this.response = response;
		this.pathPrefix = pathPrefix;
		this.queryContext = new QueryContext();
		this.queryContext.setBaseUrl(getBaseUrl());
		this.queryContext.setRequestPath(getPath());
	}
	
	@Override
	public QueryContext getQueryContext() {
		return queryContext;
	}
	
	@Override
	public boolean hasResponse() {
		return httpResponse != null;
	}
	
	@Override
	public boolean acceptsAny() {
		String accept = request.getHeader("Accept");
		return accept == null || accept.isEmpty() || accept.contains("*/*");
	}
	
	@Override
	public boolean accepts(String contentType) {
		String accept = request.getHeader("Accept");
		if (accept == null || accept.isEmpty() || accept.contains("*/*")) {
			return true;
		}
		return accept.contains(contentType);
	}
	
	@Override
	public String getMethod() {
		return request.getMethod();
	}
	
	@Override
	public java.net.URI getNativeRequestUri() {
		try {
			return new java.net.URI(request.getRequestURI());
		} catch (java.net.URISyntaxException e) {
			throw new RuntimeException("Invalid request URI", e);
		}
	}

	@Override
	public Set<String> getRequestHeaderNames() {
		return Collections.list(request.getHeaderNames()).stream().collect(Collectors.toSet());
	}

	@Override
	public String getPath() {
		String path = request.getRequestURI();
		String contextPath = request.getContextPath();
		if (contextPath != null && !contextPath.isEmpty()) {
			path = path.substring(contextPath.length());
		}
		// Remove the path prefix to get the crnk-relative path
		if (path.startsWith(pathPrefix)) {
			path = path.substring(pathPrefix.length());
		}
		return path;
	}
	
	@Override
	public String getBaseUrl() {
		StringBuilder url = new StringBuilder();
		url.append(request.getScheme());
		url.append("://");
		url.append(request.getServerName());
		int port = request.getServerPort();
		if ((request.getScheme().equals("http") && port != 80) ||
			(request.getScheme().equals("https") && port != 443)) {
			url.append(":").append(port);
		}
		String contextPath = request.getContextPath();
		if (contextPath != null && !contextPath.isEmpty()) {
			url.append(contextPath);
		}
		url.append(pathPrefix);
		return url.toString();
	}
	
	@Override
	public Map<String, Set<String>> getRequestParameters() {
		Map<String, Set<String>> params = new HashMap<>();
		request.getParameterMap().forEach((key, values) -> {
			params.put(key, Set.of(values));
		});
		return params;
	}
	
	@Override
	public String getRequestHeader(String name) {
		return request.getHeader(name);
	}
	
	// Note: Not an interface method - helper for internal use
	public Map<String, String> getRequestHeaders() {
		Map<String, String> headers = new HashMap<>();
		var headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			headers.put(name, request.getHeader(name));
		}
		return headers;
	}
	
	@Override
	public byte[] getRequestBody() {
		if (requestBody == null) {
			try {
				InputStream is = request.getInputStream();
				requestBody = is.readAllBytes();
			} catch (IOException e) {
				throw new RuntimeException("Failed to read request body", e);
			}
		}
		return requestBody;
	}
	
	@Override
	public Object getRequestAttribute(String name) {
		Object value = requestAttributes.get(name);
		if (value == null) {
			value = request.getAttribute(name);
		}
		return value;
	}
	
	@Override
	public void setRequestAttribute(String name, Object value) {
		requestAttributes.put(name, value);
		request.setAttribute(name, value);
	}
	
	@Override
	public void setResponse(HttpResponse response) {
		this.httpResponse = response;
	}
	
	@Override
	public HttpResponse getResponse() {
		return httpResponse;
	}
	
	@Override
	public <T> T unwrap(Class<T> type) {
		if (type.isInstance(request)) {
			return type.cast(request);
		}
		if (type.isInstance(response)) {
			return type.cast(response);
		}
		return null;
	}
	
	/**
	 * Flush the crnk response to the servlet response.
	 */
	public void flushResponse() {
		if (httpResponse != null) {
			try {
				response.setStatus(httpResponse.getStatusCode());
				
				// Set headers
				if (httpResponse.getHeaders() != null) {
					httpResponse.getHeaders().forEach((name, value) -> {
						response.setHeader(name, value);
					});
				}
				
				// Write body
				byte[] body = httpResponse.getBody();
				if (body != null && body.length > 0) {
					OutputStream os = response.getOutputStream();
					os.write(body);
					os.flush();
				}
			} catch (IOException e) {
				throw new RuntimeException("Failed to write response", e);
			}
		}
	}

}
