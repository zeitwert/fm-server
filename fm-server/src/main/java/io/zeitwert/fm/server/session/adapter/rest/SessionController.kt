package io.zeitwert.fm.server.session.adapter.rest

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountDtoAdapter
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole.Enumeration.getUserRole
import io.zeitwert.fm.server.config.security.AppUserDetails
import io.zeitwert.fm.server.session.adapter.rest.dto.LoginRequest
import io.zeitwert.fm.server.session.adapter.rest.dto.LoginResponse
import io.zeitwert.fm.server.session.adapter.rest.dto.SessionInfoResponse
import io.zeitwert.fm.server.session.version.ApplicationInfo
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController("sessionController")
@RequestMapping("/rest/session")
class SessionController {

	private val logger: Logger = LoggerFactory.getLogger(SessionController::class.java)

	@Autowired
	lateinit var authenticationManager: AuthenticationManager

	@Autowired
	lateinit var tenantRepository: ObjTenantRepository

	@Autowired
	lateinit var accountRepository: ObjAccountRepository

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var tenantDtoAdapter: ObjTenantDtoAdapter

	@Autowired
	lateinit var accountDtoAdapter: ObjAccountDtoAdapter

	@Autowired
	lateinit var userDtoAdapter: ObjUserDtoAdapter

	@PostMapping("/login")
	fun login(
		@RequestBody loginRequest: LoginRequest,
		request: HttpServletRequest,
	): ResponseEntity<LoginResponse?> {
		try {
			val authToken = getAuthToken(loginRequest.email, loginRequest.password)
			val authentication = authenticationManager.authenticate(authToken)

			// Get user details and set tenantId/accountId
			val userDetails = authentication.principal as AppUserDetails
			val tenantId = loginRequest.tenantId
			require(tenantId != null) { "tenantId not null" }
			userDetails._tenantId = tenantId
			userDetails.accountId = loginRequest.accountId

			// Create authentication with updated user details
			val updatedAuthentication = UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.authorities,
			)
			updatedAuthentication.details = WebAuthenticationDetailsSource().buildDetails(request)

			// Set security context
			val securityContext = SecurityContextHolder.createEmptyContext()
			securityContext.authentication = updatedAuthentication
			SecurityContextHolder.setContext(securityContext)

			// Create session and store security context
			val session = request.getSession(true)
			session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext)

			val role = userDetails.authorities.map { it.authority }[0]
			val loginResponse = LoginResponse(
				sessionId = session.id,
				id = userDetails.userId as Int,
				username = userDetails.username,
				email = userDetails.username,
				accountId = loginRequest.accountId,
				role = EnumeratedDto.of(getUserRole(role)),
			)
			return ResponseEntity.ok<LoginResponse?>(loginResponse)
		} catch (ex: Exception) {
			logger.error("Login failed: $ex")
			throw RuntimeException(ex)
		}
	}

	private fun getAuthToken(
		email: String?,
		password: String?,
	): Authentication = UsernamePasswordAuthenticationToken(email, password)

	@GetMapping("/session")
	fun getSessionInfo(): ResponseEntity<SessionInfoResponse> {
		val tenantId = sessionContext.tenantId
		val tenant = tenantRepository.get(tenantId)
		val accountId = sessionContext.accountId
		val account = if (accountId != null) accountRepository.get(accountId) else null
		val user = sessionContext.user
		val defaultApp = if (user.isAppAdmin) {
			"appAdmin"
		} else if (user.isAdmin) {
			"tenantAdmin"
		} else {
			"fm"
		}
		val response = SessionInfoResponse(
			applicationName = ApplicationInfo.getName(),
			applicationVersion = ApplicationInfo.getVersion(),
			user = userDtoAdapter.fromAggregate(user),
			tenant = tenantDtoAdapter.fromAggregate(tenant),
			account = if (account != null) accountDtoAdapter.fromAggregate(account) else null,
			locale = sessionContext.locale.id,
			applicationId = defaultApp,
			availableApplications = listOf(defaultApp),
		)
		return ResponseEntity.ok(response)
	}

	@PostMapping("/logout")
	fun logout(
		request: HttpServletRequest,
		response: HttpServletResponse,
	): ResponseEntity<String?> {
		// Invalidate session
		val session = request.getSession(false)
		session?.invalidate()

		// Delete JSESSIONID cookie
		val cookie = Cookie("JSESSIONID", null)
		cookie.path = "/"
		cookie.maxAge = 0
		cookie.isHttpOnly = true
		response.addCookie(cookie)

		// Clear security context
		SecurityContextHolder.clearContext()

		return ResponseEntity.ok().build<String?>()
	}

	@ExceptionHandler(Exception::class)
	@ResponseBody
	fun handleException(e: Exception): ResponseEntity<Any?> {
		e.printStackTrace()
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<Any?>(e.message)
	}

}
