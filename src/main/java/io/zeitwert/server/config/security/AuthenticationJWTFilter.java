package io.zeitwert.server.config.security;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.zeitwert.server.session.service.api.JwtProvider;

public class AuthenticationJWTFilter extends OncePerRequestFilter {

	public static final String AUTH_HEADER_PREFIX = "Bearer ";

	@Value("${zeitwert.app.jwtSecret}")
	private String jwtSecret;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String authToken = this.getJwtFromHeader(request);
		if (StringUtils.hasText(authToken)) {

			try {

				Claims claims = this.getClaims(authToken);
				String userEmail = claims.getSubject();
				if (!StringUtils.hasText(userEmail)) {
					throw new RuntimeException("Authentication error (invalid email claim)");
				}

				ZeitwertUserDetails userDetails = (ZeitwertUserDetails) userDetailsService.loadUserByUsername(userEmail);
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

			} catch (ExpiredJwtException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} catch (Exception exception) {
				throw new RuntimeException("Authentication error", exception);
			}

		}

		chain.doFilter(request, response);

	}

	private String getJwtFromHeader(HttpServletRequest request) {
		if (!this.hasJwt(request)) {
			return null;
		}
		String authHeader = request.getHeader("Authorization");
		return authHeader.substring(AUTH_HEADER_PREFIX.length());
	}

	private boolean hasJwt(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		return StringUtils.hasText(authHeader) && authHeader.startsWith(AUTH_HEADER_PREFIX);
	}

	public boolean isValidToken(Claims claims, UserDetails userDetails) {
		final String user = claims.getSubject();
		return (user.equals(userDetails.getUsername()) && !isTokenExpired(claims));
	}

	private boolean isTokenExpired(Claims claims) {
		Date expiration = claims.getExpiration();
		return expiration.before(new Date());
	}

	private Claims getClaims(String token) {
		requireThis(token != null && token.length() > 0, "valid JWT (" + token + ")");
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
	}

}
