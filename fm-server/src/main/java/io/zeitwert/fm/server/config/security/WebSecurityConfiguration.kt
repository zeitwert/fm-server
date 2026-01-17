package io.zeitwert.fm.server.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.session.HttpSessionEventPublisher

private val GET_PERMIT_ALL = arrayOf(
	// enumerations
	"/enum/**",
	// static content
	"/*",
	"/static/**",
	"/assets/**",
	"/images/**",
	// ui paths (necessary for hyperlinks)
	"/home/*",
	"/tenant/*",
	"/user/*",
	"/account/*",
	"/contact/*",
	"/portfolio/*",
	"/building/*",
	"/task/*",
	// user, tenant and account pictures
	"/rest/oe/users/**/avatar",
	"/rest/oe/tenants/**/logo",
	"/rest/oe/tenants/**/banner",
	"/rest/account/accounts/**/logo",
	"/rest/account/accounts/**/banner",
	// special paths via <img src=\"\" />
	"/rest/dms/documents/**/content",
	"/rest/building/buildings/*/evaluation/**",
	"/rest/portfolio/portfolios/*/evaluation/**",
	// test paths
	"/rest/test/all",
)

private val POST_PERMIT_ALL = arrayOf(
	"/rest/session/login/**",
	"/rest/session/authenticate",
)

@Configuration
@EnableWebSecurity
open class WebSecurityConfiguration(
	private val unauthorizedHandler: AppAuthenticationEntryPoint,
) {

	@Bean
	@Throws(Exception::class)
	open fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager = authConfig.authenticationManager

	@Bean
	open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	open fun httpSessionEventPublisher(): HttpSessionEventPublisher = HttpSessionEventPublisher()

	@Bean
	// TODO Revoke unnecessary permissions
	@Throws(Exception::class)
	open fun filterChain(http: HttpSecurity): SecurityFilterChain {
		// Spring Security 6 lambda DSL
		http
			.cors { cors -> cors.configure(http) }
			.csrf { csrf -> csrf.disable() }
			.headers { headers -> headers.frameOptions { frame -> frame.disable() } }
			.exceptionHandling { ex -> ex.authenticationEntryPoint(unauthorizedHandler) }
			.sessionManagement { session ->
				session
					.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
					.maximumSessions(1)
					.maxSessionsPreventsLogin(false)
			}

		http
			.authorizeHttpRequests { auth ->
				auth
					.requestMatchers(HttpMethod.GET, *GET_PERMIT_ALL)
					.permitAll()
					.requestMatchers(HttpMethod.POST, *POST_PERMIT_ALL)
					.permitAll()
					.requestMatchers(HttpMethod.GET, "/rest/test/**")
					.authenticated()
					.anyRequest()
					.authenticated()
			}

		return http.build()
	}

}
