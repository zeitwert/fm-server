package io.zeitwert.fm.server.session.adapter.rest

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto.Companion.of
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountLoginDtoAdapter
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjUserDtoAdapter
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.enums.CodeUserRole.Enumeration.getUserRole
import io.zeitwert.fm.server.config.security.ZeitwertUserDetails
import io.zeitwert.fm.server.session.adapter.rest.dto.LoginRequest
import io.zeitwert.fm.server.session.adapter.rest.dto.LoginResponse
import io.zeitwert.fm.server.session.adapter.rest.dto.SessionInfoResponse
import io.zeitwert.fm.server.session.service.api.JwtProvider
import io.zeitwert.fm.server.session.version.ApplicationInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
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
	lateinit var jwtProvider: JwtProvider

	@Autowired
	lateinit var requestCtx: RequestContextFM

	@Autowired
	lateinit var tenantDtoAdapter: ObjTenantDtoAdapter

	@Autowired
	lateinit var accountDtoAdapter: ObjAccountLoginDtoAdapter

	@Autowired
	lateinit var userDtoAdapter: ObjUserDtoAdapter

	@PostMapping("/login")
	fun login(
		@RequestBody loginRequest: LoginRequest,
	): ResponseEntity<LoginResponse?> {
		try {
			val authToken = this.getAuthToken(loginRequest.email, loginRequest.password)
			val authentication = this.authenticationManager.authenticate(authToken)
			SecurityContextHolder.getContext().authentication = authentication
			val tenantId = loginRequest.tenantId
			require(tenantId != null) { "tenantId not null" }
			val accountId = loginRequest.accountId
			val jwt = this.jwtProvider.createJwt(authentication, tenantId, accountId)
			val userDetails = authentication.principal as ZeitwertUserDetails
			val role =
				userDetails
					.authorities
					.stream()
					.map<String> { item: GrantedAuthority? -> item!!.authority }
					.toList()
					.get(0)
			val loginResponse = LoginResponse(
				token = jwt,
				id = userDetails.userId,
				username = userDetails.username,
				email = userDetails.username,
				accountId = accountId,
				role = of(getUserRole(role)),
			)
			return ResponseEntity.ok<LoginResponse?>(loginResponse)
		} catch (ex: Exception) {
			this.logger.error("Login failed: $ex")
			throw RuntimeException(ex)
		}
	}

	private fun getAuthToken(
		email: String?,
		password: String?,
	): Authentication = UsernamePasswordAuthenticationToken(email, password)

	@get:GetMapping("/session")
	val requestContext: ResponseEntity<SessionInfoResponse?>
		get() {
			// if (this.requestCtx == null) {
			// 	return ResponseEntity
			// 		.status(HttpStatus.UNAUTHORIZED)
			// 		.build<SessionInfoReponse?>()
			// }
			val tenant = this.tenantRepository.get(this.requestCtx.getTenantId())
			val account =
				if (this.requestCtx.hasAccount()) this.accountRepository.get(this.requestCtx.getAccountId()) else null
			val user = this.requestCtx.getUser() as ObjUser
			var defaultApp: String? = null
			if (user.isAppAdmin) {
				defaultApp = "appAdmin"
			} else if (user.isAdmin) {
				defaultApp = "tenantAdmin"
			} else {
				defaultApp = "fm"
			}
			val response = SessionInfoResponse(
				applicationName = ApplicationInfo.getName(),
				applicationVersion = ApplicationInfo.getVersion(),
				user = this.userDtoAdapter.fromAggregate(this.requestCtx.getUser() as ObjUser?),
				tenant = this.tenantDtoAdapter.fromAggregate(tenant),
				account = this.accountDtoAdapter.fromAggregate(account),
				locale = this.requestCtx.getLocale().id,
				applicationId = defaultApp,
				availableApplications = emptyList(),
			)
			return ResponseEntity.ok<SessionInfoResponse?>(response)
		}

	@PostMapping("/logout")
	fun logout(
		@RequestHeader("Authorization") authHeader: String?,
	): ResponseEntity<String?> {
		if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_PREFIX)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<String?>()
		}
		// TODO logout
		return ResponseEntity.ok().build<String?>()
	}

	@ExceptionHandler(Exception::class)
	@ResponseBody
	fun handleException(e: Exception): ResponseEntity<Any?> {
		e.printStackTrace()
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<Any?>(e.message)
	}

	companion object {

		const val AUTH_HEADER_PREFIX: String = "Bearer "
	}

}
