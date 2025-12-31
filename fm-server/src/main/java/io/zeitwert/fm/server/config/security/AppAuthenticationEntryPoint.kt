package io.zeitwert.fm.server.config.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class AppAuthenticationEntryPoint : AuthenticationEntryPoint {

	@Throws(IOException::class, ServletException::class)
	override fun commence(
		request: HttpServletRequest,
		response: HttpServletResponse,
		authException: AuthenticationException,
	) {
		logger.error("Unauthorized error at {}: {}", request.requestURI, authException.message, authException)
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized")
	}

	companion object {

		private val logger: Logger = LoggerFactory.getLogger(AppAuthenticationEntryPoint::class.java)
	}

}
