package io.zeitwert.server.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; TODO
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.zeitwert.server.session.SessionCookieFilter;
import io.zeitwert.server.session.jwt.AuthEntryPointJwt;
import io.zeitwert.server.session.jwt.AuthTokenFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService)/* .passwordEncoder(passwordEncoder()) */;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// @Bean
	// public PasswordEncoder passwordEncoder() {
	// return new BCryptPasswordEncoder();
	// }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//@formatter:off
		http.cors().and()
			.csrf().disable()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
			.authorizeRequests()
				.antMatchers("/*").permitAll()
				.antMatchers("/static/**").permitAll()
				.antMatchers("/assets/**").permitAll()
				.antMatchers("/demo/**").permitAll()
				.antMatchers("/api/app/userInfo/**").permitAll() // TODO revoke
				.antMatchers("/api/session/login/**").permitAll()
				.antMatchers("/enum/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/building/projection/**").permitAll() // TODO revoke
				.antMatchers(HttpMethod.GET, "/rest/dms/documents/**/content").permitAll() // TODO revoke
				.antMatchers(HttpMethod.GET, "/rest/building/buildings/location/{id:\\w+}").permitAll() // TODO revoke
				.antMatchers(HttpMethod.GET, "/evaluation/building/buildings/**").permitAll() // TODO revoke
				.antMatchers("/api/test/all").permitAll()
				.antMatchers("/api/test/**").authenticated()
			.anyRequest().authenticated();
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterAfter(new SessionCookieFilter(), FilterSecurityInterceptor.class);
		//@formatter:on
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(List.of("*"));
		// configuration.setAllowedOrigins(List.of("*"));
		config.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
		// setAllowCredentials(true) is important, otherwise:
		// The value of the 'Access-Control-Allow-Origin' header in the response must
		// not be the wildcard '*' when the request's credentials mode is 'include'.
		config.setAllowCredentials(true);
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		// will fail with 403 Invalid CORS request
		config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
		config.addExposedHeader("Content-Disposition"); // allow access to content-disposition for file downloads
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

}
