package io.zeitwert.fm.server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfiguration {

	@Autowired
	private ZeitwertAuthenticationEntryPoint unauthorizedHandler;

	@Bean
	public AuthenticationManager authenticationManager(
			HttpSecurity http,
			PasswordEncoder bCryptPasswordEncoder,
			UserDetailsService userDetailsService)
			throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.passwordEncoder(bCryptPasswordEncoder)
				.and()
				.build();
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
		http.cors().and()
				.csrf().disable()
				.exceptionHandling().authenticationEntryPoint(this.unauthorizedHandler).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests()
				// monitoring
				// .antMatchers("/actuator/**").permitAll()
				// login
				.antMatchers(HttpMethod.GET, "/rest/app/**").permitAll()
				.antMatchers(HttpMethod.POST, "/rest/session/login/**").permitAll()
				// enumerations
				.antMatchers(HttpMethod.GET, "/enum/**").permitAll()
				// static content
				.antMatchers(HttpMethod.GET, "/*").permitAll()
				.antMatchers(HttpMethod.GET, "/static/**").permitAll()
				.antMatchers(HttpMethod.GET, "/assets/**").permitAll()
				.antMatchers(HttpMethod.GET, "/images/**").permitAll()
				// ui paths (necessary for hyperlinks, since JWT is not propagated)
				.antMatchers(HttpMethod.GET, "/home/*").permitAll()
				.antMatchers(HttpMethod.GET, "/tenant/*").permitAll()
				.antMatchers(HttpMethod.GET, "/user/*").permitAll()
				.antMatchers(HttpMethod.GET, "/account/*").permitAll()
				.antMatchers(HttpMethod.GET, "/contact/*").permitAll()
				.antMatchers(HttpMethod.GET, "/portfolio/*").permitAll()
				.antMatchers(HttpMethod.GET, "/building/*").permitAll()
				.antMatchers(HttpMethod.GET, "/task/*").permitAll()
				// user, tenant and account pictures
				.antMatchers(HttpMethod.GET, "/rest/oe/users/**/avatar").permitAll()
				.antMatchers(HttpMethod.GET, "/rest/oe/tenants/**/{logo|banner}").permitAll()
				.antMatchers(HttpMethod.GET, "/rest/account/accounts/**/{logo|banner}").permitAll()
				// special paths via <img src="" />
				.antMatchers(HttpMethod.GET, "/rest/dms/documents/**/content").permitAll() // revoke
				.antMatchers(HttpMethod.GET, "/rest/building/buildings/*/evaluation/**").permitAll() // revoke
				.antMatchers(HttpMethod.GET, "/rest/portfolio/portfolios/*/evaluation/**").permitAll() // revoke
				// statistics
				.antMatchers(HttpMethod.GET, "/**/statistics").permitAll()
				// test paths
				.antMatchers(HttpMethod.GET, "/rest/test/all").permitAll()
				.antMatchers(HttpMethod.GET, "/rest/test/**").authenticated()
				.anyRequest().authenticated();
		http.addFilterBefore(this.authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		http.headers().frameOptions().disable();
		return http.build();
	}

}
