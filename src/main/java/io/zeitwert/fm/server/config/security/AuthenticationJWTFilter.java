package io.zeitwert.fm.server.config.security;

import static io.dddrive.util.Invariant.requireThis;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.zeitwert.fm.server.session.service.api.JwtProvider;

public class AuthenticationJWTFilter extends OncePerRequestFilter {

	private Logger logger = LoggerFactory.getLogger(AuthenticationJWTFilter.class);

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		try {

			String authToken = this.getJwtFromHeader(request);

			if (StringUtils.hasText(authToken)) {

				Claims claims = this.getClaims(authToken);
				String userEmail = claims.getSubject();
				if (!StringUtils.hasText(userEmail)) {
					throw new RuntimeException("Authentication error (invalid email claim)");
				}

				ZeitwertUserDetails userDetails = (ZeitwertUserDetails) this.userDetailsService.loadUserByUsername(userEmail);
				if (!this.isValidToken(claims, userDetails)) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}

				Integer tenantId = (Integer) claims.get(JwtProvider.TENANT_CLAIM);
				if (tenantId == null) {
					throw new RuntimeException("Authentication error (invalid tenantId claim)");
				}
				userDetails.setTenantId(tenantId);

				Integer accountId = (Integer) claims.get(JwtProvider.ACCOUNT_CLAIM);
				if (!(userDetails.isAdmin() || userDetails.isAppAdmin()) && accountId == null) {
					throw new RuntimeException("Authentication error (invalid accountId claim)");
				}
				userDetails.setAccountId(accountId);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
						null,
						userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);

			}

		} catch (Exception ex) {
			this.logger.error("authentication failed: " + ex.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		try {

			chain.doFilter(request, response);

		} catch (Exception ex) {
			this.logger.error("request crashed: " + ex.getMessage(), ex);
			ex.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}

	private String getJwtFromHeader(HttpServletRequest request) {
		if (!this.hasJwt(request)) {
			return null;
		}
		String authHeader = request.getHeader("Authorization");
		return authHeader.substring(JwtProvider.AUTH_HEADER_PREFIX.length());
	}

	private boolean hasJwt(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		return StringUtils.hasText(authHeader) && authHeader.startsWith(JwtProvider.AUTH_HEADER_PREFIX);
	}

	public boolean isValidToken(Claims claims, UserDetails userDetails) {
		final String user = claims.getSubject();
		return (user.equals(userDetails.getUsername()) && !this.isTokenExpired(claims));
	}

	private boolean isTokenExpired(Claims claims) {
		Date expiration = claims.getExpiration();
		return expiration.before(new Date());
	}

	private Claims getClaims(String token) {
		requireThis(token != null && token.length() > 0, "valid JWT (" + token + ")");
		return Jwts.parserBuilder()
				.setSigningKey(JwtProvider.JWT_SECRET_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

}
