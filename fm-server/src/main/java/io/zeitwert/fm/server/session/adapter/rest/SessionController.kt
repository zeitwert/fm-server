package io.zeitwert.fm.server.session.adapter.rest

import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.adapter.jsonapi.impl.ObjAccountDtoAdapter
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.oe.adapter.jsonapi.impl.ObjTenantDtoAdapter
import io.zeitwert.fm.oe.adapter.jsonapi.impl.ObjUserDtoAdapter
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole.Enumeration.getUserRole
import io.zeitwert.fm.server.config.security.AppUserDetails
import io.zeitwert.fm.server.session.adapter.rest.dto.ActivateRequest
import io.zeitwert.fm.server.session.adapter.rest.dto.AuthenticateRequest
import io.zeitwert.fm.server.session.adapter.rest.dto.AuthenticateResponse
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
	lateinit var userRepository: ObjUserRepository

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

	/**
	 * Phase 1: Authenticate user with email/password.
	 * Creates a pre-session (authenticated but not fully activated).
	 * Returns user info including available tenants.
	 */
	@PostMapping("/authenticate")
	fun authenticate(
		@RequestBody request: AuthenticateRequest,
		httpRequest: HttpServletRequest,
	): ResponseEntity<AuthenticateResponse> {
		try {
			val authToken = getAuthToken(request.email, request.password)
			val authentication = authenticationManager.authenticate(authToken)

			val userDetails = authentication.principal as AppUserDetails

			// Create authentication without tenantId/accountId yet
			val preAuthentication = UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.authorities,
			)
			preAuthentication.details = WebAuthenticationDetailsSource().buildDetails(httpRequest)

			// Set security context
			val securityContext = SecurityContextHolder.createEmptyContext()
			securityContext.authentication = preAuthentication
			SecurityContextHolder.setContext(securityContext)

			// Create session and store security context
			val session = httpRequest.getSession(true)
			session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext)

			// Get user with tenant list
			val user = userRepository.get(userDetails.userId)
			val role = userDetails.authorities.map { it.authority }[0]
			val tenants = user.tenantSet.map { EnumeratedDto.of(tenantRepository.get(it))!! }

			val response = AuthenticateResponse(
				id = userDetails.userId as Int,
				name = user.name!!,
				email = user.email!!,
				role = EnumeratedDto.of(getUserRole(role))!!,
				tenants = tenants,
			)
			return ResponseEntity.ok(response)
		} catch (ex: Exception) {
			logger.error("Authentication failed: $ex")
			throw RuntimeException(ex)
		}
	}

	/**
	 * Phase 2: Activate session with tenant and optional account.
	 * Requires prior authentication via /authenticate.
	 * Returns full session info.
	 */
	@PostMapping("/activate")
	fun activate(
		@RequestBody request: ActivateRequest,
		httpRequest: HttpServletRequest,
	): ResponseEntity<SessionInfoResponse> {
		try {
			val auth = SecurityContextHolder.getContext().authentication
			check(auth != null && auth.principal is AppUserDetails) { "User must be authenticated first" }

			val userDetails = auth.principal as AppUserDetails
			val user = userRepository.get(userDetails.userId)

			// Validate tenant switching is not allowed
			val existingTenantId = userDetails._tenantId
			if (existingTenantId != null && existingTenantId != request.tenantId) {
				throw IllegalArgumentException(
					"Cannot switch tenant during session. Current: $existingTenantId, Requested: ${request.tenantId}"
				)
			}

			// Validate tenant is allowed for user
			check(user.tenantSet.contains(request.tenantId)) {
				"User does not have access to tenant ${request.tenantId}"
			}

			// Validate account belongs to the current tenant
			if (request.accountId != null) {
				val account = accountRepository.get(request.accountId)
				check(account.tenantId == request.tenantId) {
					"Account ${request.accountId} does not belong to tenant ${request.tenantId}"
				}
			}

			// Set tenantId and accountId
			userDetails._tenantId = request.tenantId
			userDetails.accountId = request.accountId

			// Create updated authentication with tenant/account
			val updatedAuthentication = UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.authorities,
			)
			updatedAuthentication.details = WebAuthenticationDetailsSource().buildDetails(httpRequest)

			// Update security context
			val securityContext = SecurityContextHolder.createEmptyContext()
			securityContext.authentication = updatedAuthentication
			SecurityContextHolder.setContext(securityContext)

			// Update session with new security context
			val session = httpRequest.getSession(false)
			session?.setAttribute("SPRING_SECURITY_CONTEXT", securityContext)

			// Build session info response
			val tenant = tenantRepository.get(request.tenantId)
			val account = if (request.accountId != null) accountRepository.get(request.accountId) else null
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
				locale = "de_CH",
				applicationId = defaultApp,
				availableApplications = listOf(defaultApp),
			)
			return ResponseEntity.ok(response)
		} catch (ex: Exception) {
			logger.error("Session activation failed: $ex")
			throw RuntimeException(ex)
		}
	}

	/**
	 * Legacy login endpoint - requires tenantId upfront.
	 * @deprecated Use /authenticate followed by /activate instead.
	 */
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
		val user = userRepository.get(sessionContext.userId)
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
