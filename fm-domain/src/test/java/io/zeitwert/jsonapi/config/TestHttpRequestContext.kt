package io.zeitwert.jsonapi.config

import io.crnk.core.engine.http.DefaultHttpRequestContextBase
import io.crnk.core.engine.http.HttpRequestContext
import io.crnk.core.engine.http.HttpResponse
import io.crnk.core.engine.query.QueryContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.net.URISyntaxException

/**
 * Jakarta Servlet API compatible HttpRequestContext implementation for Crnk.
 *
 * This bridges jakarta.servlet (Spring Boot 3) to crnk's internal HTTP abstraction.
 */
class TestHttpRequestContext(
	private val request: HttpServletRequest,
	private val response: HttpServletResponse,
	private val pathPrefix: String,
) : DefaultHttpRequestContextBase(),
	HttpRequestContext {

	private val queryContext: QueryContext = QueryContext()

	private val requestAttributes: MutableMap<String, Any?> = mutableMapOf()
	private var requestBody: ByteArray? = null
	private var httpResponse: HttpResponse? = null

	init {
		this.queryContext.baseUrl = getBaseUrl()
		this.queryContext.requestPath = getPath()
	}

	override fun getQueryContext(): QueryContext = queryContext

	override fun hasResponse(): Boolean = httpResponse != null

	override fun acceptsAny(): Boolean {
		val accept = request.getHeader("Accept")
		return accept == null || accept.isEmpty() || accept.contains("*/*")
	}

	override fun accepts(contentType: String): Boolean {
		val accept = request.getHeader("Accept")
		if (accept == null || accept.isEmpty() || accept.contains("*/*")) {
			return true
		}
		return accept.contains(contentType)
	}

	override fun getMethod(): String = request.method

	public override fun getNativeRequestUri(): URI {
		try {
			return URI(request.requestURI)
		} catch (e: URISyntaxException) {
			throw RuntimeException("Invalid request URI", e)
		}
	}

	override fun getRequestHeaderNames(): MutableSet<String> = request.headerNames.toList().toMutableSet()

	override fun getPath(): String {
		var path = request.requestURI
		val contextPath = request.contextPath
		if (contextPath != null && !contextPath.isEmpty()) {
			path = path.substring(contextPath.length)
		}
		// Remove the path prefix to get the crnk-relative path
		if (path.startsWith(pathPrefix)) {
			path = path.substring(pathPrefix.length)
		}
		return path
	}

	override fun getBaseUrl(): String {
		val url = StringBuilder()
		url.append(request.scheme)
		url.append("://")
		url.append(request.serverName)
		val port = request.serverPort
		if ((request.scheme == "http" && port != 80) ||
			(request.scheme == "https" && port != 443)
		) {
			url.append(":").append(port)
		}
		val contextPath = request.contextPath
		if (contextPath != null && !contextPath.isEmpty()) {
			url.append(contextPath)
		}
		url.append(pathPrefix)
		return url.toString()
	}

	override fun getRequestParameters(): MutableMap<String, MutableSet<String>> {
		val params = mutableMapOf<String, MutableSet<String>>()
		request.parameterMap.forEach { (key: String, values: Array<String>) ->
			// Filter out Spring Security's CSRF parameter which Crnk doesn't understand
			if (key != "_csrf") {
				params[key] = mutableSetOf(*values)
			}
		}
		return params
	}

	override fun getRequestHeader(name: String): String? = request.getHeader(name)

	val requestHeaders: Map<String, String>
		get() {
			val headers = mutableMapOf<String, String>()
			val headerNames = request.headerNames
			while (headerNames.hasMoreElements()) {
				val name = headerNames.nextElement()
				headers[name] = request.getHeader(name)
			}
			return headers
		}

	override fun getRequestBody(): ByteArray? {
		if (requestBody == null) {
			try {
				val inputStream: InputStream = request.inputStream
				requestBody = inputStream.readAllBytes()
			} catch (e: IOException) {
				throw RuntimeException("Failed to read request body", e)
			}
		}
		return requestBody
	}

	override fun getRequestAttribute(name: String): Any? {
		var value = requestAttributes[name]
		if (value == null) {
			value = request.getAttribute(name)
		}
		return value
	}

	override fun setRequestAttribute(
		name: String,
		value: Any?,
	) {
		requestAttributes[name] = value
		request.setAttribute(name, value)
	}

	override fun setResponse(response: HttpResponse) {
		this.httpResponse = response
	}

	override fun getResponse(): HttpResponse? = httpResponse

	override fun <T> unwrap(type: Class<T>): T? {
		if (type.isInstance(request)) {
			return type.cast(request)
		}
		if (type.isInstance(response)) {
			return type.cast(response)
		}
		return null
	}

	/**
	 * Flush the crnk response to the servlet response.
	 */
	fun flushResponse() {
		val httpResponse = this.httpResponse
		if (httpResponse != null) {
			try {
				response.status = httpResponse.statusCode

				// Set headers
				if (httpResponse.headers != null) {
					httpResponse.headers.forEach { (name: String, value: String?) ->
						response.setHeader(name, value)
					}
				}

				// Write body
				val body = httpResponse.body
				if (body != null && body.isNotEmpty()) {
					val os: OutputStream = response.outputStream
					os.write(body)
					os.flush()
				}
			} catch (e: IOException) {
				throw RuntimeException("Failed to write response", e)
			}
		}
	}
}
