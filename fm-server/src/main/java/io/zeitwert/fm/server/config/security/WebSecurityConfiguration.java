package io.zeitwert.fm.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	@Autowired
	private ZeitwertAuthenticationEntryPoint unauthorizedHandler;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationJWTFilter authenticationJwtTokenFilter() {
		return new AuthenticationJWTFilter();
	}

	@Bean
	// TODO Revoke unnecessary permissions
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Spring Security 6 lambda DSL
		http
			.cors(cors -> cors.configure(http))
			.csrf(csrf -> csrf.disable())
			.exceptionHandling(ex -> ex.authenticationEntryPoint(this.unauthorizedHandler))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// monitoring
				// .requestMatchers("/actuator/**").permitAll()
				// login
				.requestMatchers(HttpMethod.GET, "/rest/app/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/rest/session/login/**").permitAll()
				// enumerations
				.requestMatchers(HttpMethod.GET, "/enum/**").permitAll()
				// static content
				.requestMatchers(HttpMethod.GET, "/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/static/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/assets/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/images/**").permitAll()
				// ui paths (necessary for hyperlinks, since JWT is not propagated)
				.requestMatchers(HttpMethod.GET, "/home/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/tenant/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/user/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/account/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/contact/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/portfolio/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/building/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/task/*").permitAll()
				// user, tenant and account pictures
				.requestMatchers(HttpMethod.GET, "/rest/oe/users/**/avatar").permitAll()
				.requestMatchers(HttpMethod.GET, "/rest/oe/tenants/**/logo", "/rest/oe/tenants/**/banner").permitAll()
				.requestMatchers(HttpMethod.GET, "/rest/account/accounts/**/logo", "/rest/account/accounts/**/banner").permitAll()
				// special paths via <img src="" />
				.requestMatchers(HttpMethod.GET, "/rest/dms/documents/**/content").permitAll() // revoke
				.requestMatchers(HttpMethod.GET, "/rest/building/buildings/*/evaluation/**").permitAll() // revoke
				.requestMatchers(HttpMethod.GET, "/rest/portfolio/portfolios/*/evaluation/**").permitAll() // revoke
				// statistics
				.requestMatchers(HttpMethod.GET, "/**/statistics").permitAll()
				// test paths
				.requestMatchers(HttpMethod.GET, "/rest/test/all").permitAll()
				.requestMatchers(HttpMethod.GET, "/rest/test/**").authenticated()
				.anyRequest().authenticated()
			);
		
		http.addFilterBefore(this.authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
		
		return http.build();
	}

}
