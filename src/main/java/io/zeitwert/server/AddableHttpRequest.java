package io.zeitwert.server;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class AddableHttpRequest extends HttpServletRequestWrapper {

	/**
	 * A map containing additional request params this wrapper added to the wrapped
	 * request.
	 */
	private final Map<String, String> params = new HashMap<>();

	/**
	 * A map containing additional header params this wrapper added to the wrapped
	 * request.
	 */
	private final Map<String, String> headers = new HashMap<String, String>();

	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @throws java.lang.IllegalArgumentException if the request is null
	 */
	public AddableHttpRequest(final HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(final String name) {
		if (params.get(name) != null) {
			return params.get(name);
		}

		return super.getParameter(name);
	}

	/**
	 * add a header with given name and value.
	 * 
	 * @param name
	 * @param value
	 */
	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	/**
	 * Add a header with given name and value.
	 *
	 * @param name
	 * @param value
	 */
	public void addParameter(String name, String value) {
		params.put(name, value);
	}

	@Override
	public String getHeader(String name) {
		String headerValue = super.getHeader(name);
		if (headers.containsKey(name)) {
			headerValue = headers.get(name);
		}
		return headerValue;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		List<String> names = Collections.list(super.getHeaderNames());
		for (String name : headers.keySet()) {
			names.add(name);
		}
		return Collections.enumeration(names);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		List<String> values = Collections.list(super.getHeaders(name));
		if (headers.containsKey(name)) {
			values.add(headers.get(name));
		}
		return Collections.enumeration(values);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return super.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return super.getParameterNames();
	}

	@Override
	public String[] getParameterValues(final String name) {
		return super.getParameterValues(name);
	}

}