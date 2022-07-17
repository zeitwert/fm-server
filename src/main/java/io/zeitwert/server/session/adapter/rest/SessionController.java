
package io.zeitwert.server.session.adapter.rest;

import java.util.List;

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

import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.server.config.security.ZeitwertUserDetails;
import io.zeitwert.server.session.adapter.rest.dto.LoginRequest;
import io.zeitwert.server.session.adapter.rest.dto.LoginResponse;
import io.zeitwert.server.session.adapter.rest.dto.SessionInfoReponse;
import io.zeitwert.server.session.service.api.JwtProvider;
import io.zeitwert.server.session.service.api.SessionService;

@RestController("sessionController")
@RequestMapping("/api/session")
public class SessionController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	ObjUserRepository userRepository;

	@Autowired
	ObjAccountRepository accountRepository;

	@Autowired
	JwtProvider jwtProvider;

	@Autowired
	SessionInfo sessionInfo;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		Authentication authToken = this.getAuthToken(loginRequest);
		Authentication authentication = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		Integer accountId = loginRequest.getAccountId();
		String jwt = jwtProvider.createJwt(authentication, accountId);
		ZeitwertUserDetails userDetails = (ZeitwertUserDetails) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();
		//@formatter:off
		return ResponseEntity.ok(
			LoginResponse.builder()
				.token(jwt)
				.id(userDetails.getUserId())
				.username(userDetails.getUsername())
				.email(userDetails.getUsername())
				.accountId(accountId)
				.roles(roles)
				.build()
		);
		//@formatter:on
	}

	private Authentication getAuthToken(LoginRequest loginRequest) {
		return new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
	}

	@GetMapping("/session")
	public ResponseEntity<SessionInfoReponse> getSessionInfo() {
		if (this.sessionInfo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		return ResponseEntity.ok(SessionInfoReponse.fromSession(sessionInfo, accountRepository));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith(SessionService.AUTH_HEADER_PREFIX)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		// TODO logout
		return ResponseEntity.ok().build();
	}

}
