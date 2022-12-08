package io.zeitwert.server.config.security;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
	private ZeitwertAuthenticationEntryPoint unauthorizedHandler;

	@Bean
	public AuthenticationJWTFilter authenticationJwtTokenFilter() {
		return new AuthenticationJWTFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	// TODO Revoke unnecessary permissions
	protected void configure(HttpSecurity http) throws Exception {
		//@formatter:off
		http.cors().and()
			.csrf().disable()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
				// monitoring
				//.antMatchers("/actuator/**").permitAll()
				// login
				.antMatchers(HttpMethod.GET, "/rest/app/**").permitAll()
				.antMatchers(HttpMethod.POST, "/rest/session/login/**").permitAll()
				// enumerations
				.antMatchers(HttpMethod.GET, "/enum/**").permitAll()
				// static content
				.antMatchers(HttpMethod.GET, "/*").permitAll()
				.antMatchers(HttpMethod.GET, "/static/**").permitAll()
				.antMatchers(HttpMethod.GET, "/assets/**").permitAll()
				// ui paths (necessary for hyperlinks, since JWT is not propagated) // revoke
				.antMatchers(HttpMethod.GET, "/tenant/*").permitAll() // revoke
				.antMatchers(HttpMethod.GET, "/user/*").permitAll() // revoke
				.antMatchers(HttpMethod.GET, "/account/*").permitAll() // revoke
				.antMatchers(HttpMethod.GET, "/contact/*").permitAll() // revoke
				.antMatchers(HttpMethod.GET, "/portfolio/*").permitAll() // revoke
				.antMatchers(HttpMethod.GET, "/building/*").permitAll() // revoke
				// user, tenant and account pictures
				.antMatchers(HttpMethod.GET, "/rest/oe/users/**/avatar").permitAll()
				.antMatchers(HttpMethod.GET, "/rest/oe/tenants/**/{logo|banner}").permitAll()
				.antMatchers(HttpMethod.GET, "/rest/account/accounts/**/{logo|banner}").permitAll()
				// special paths via <img src="" />
				.antMatchers(HttpMethod.GET, "/rest/dms/documents/**/content").permitAll() // revoke
				.antMatchers(HttpMethod.GET, "/rest/building/buildings/{id:\\w+}/location").permitAll() // revoke
				// special paths via <iframe src="" />
				.antMatchers(HttpMethod.GET, "/rest/building/buildings/{id:\\w+}/evaluation/**").permitAll() // revoke
				// statistics
				.antMatchers(HttpMethod.GET, "/**/statistics").permitAll()
				// test paths
				.antMatchers(HttpMethod.GET, "/rest/test/all").permitAll()
				.antMatchers(HttpMethod.GET, "/rest/test/**").authenticated()
			.anyRequest().authenticated();
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		http.headers().frameOptions().disable();
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
