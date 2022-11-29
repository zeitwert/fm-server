
package io.zeitwert.server.session.adapter.rest;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.server.config.security.ZeitwertUserDetails;
import io.zeitwert.server.session.adapter.rest.dto.LoginRequest;
import io.zeitwert.server.session.adapter.rest.dto.LoginResponse;
import io.zeitwert.server.session.adapter.rest.dto.SessionInfoReponse;
import io.zeitwert.server.session.service.api.JwtProvider;

@RestController("sessionController")
@RequestMapping("/rest/session")
public class SessionController {

	public final static String AUTH_HEADER_PREFIX = "Bearer ";

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	ObjTenantCache tenantCache;

	@Autowired
	ObjAccountRepository accountCache;

	@Autowired
	JwtProvider jwtProvider;

	@Autowired
	RequestContext requestCtx;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		Authentication authToken = this.getAuthToken(loginRequest);
		Authentication authentication = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		Integer tenantId = loginRequest.getTenantId();
		requireThis(tenantId != null, "tenantId not null");
		Integer accountId = loginRequest.getAccountId();
		String jwt = jwtProvider.createJwt(authentication, tenantId, accountId);
		ZeitwertUserDetails userDetails = (ZeitwertUserDetails) authentication.getPrincipal();
		String role = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList().get(0);
		//@formatter:off
		return ResponseEntity.ok(
			LoginResponse.builder()
				.token(jwt)
				.id(userDetails.getUserId())
				.username(userDetails.getUsername())
				.email(userDetails.getUsername())
				.accountId(accountId)
				.role(EnumeratedDto.fromEnum(CodeUserRoleEnum.getUserRole(role)))
				.build()
		);
		//@formatter:on
	}

	private Authentication getAuthToken(LoginRequest loginRequest) {
		return new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
	}

	@GetMapping("/session")
	public ResponseEntity<SessionInfoReponse> getRequestContext() {
		if (this.requestCtx == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		ObjTenant tenant = tenantCache.get(this.requestCtx.getTenantId());
		ObjAccount account = requestCtx.hasAccount() ? this.accountCache.get(requestCtx.getAccountId()) : null;
		return ResponseEntity.ok(SessionInfoReponse.fromRequest(requestCtx, tenant, account));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_PREFIX)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		// TODO logout
		return ResponseEntity.ok().build();
	}

}
