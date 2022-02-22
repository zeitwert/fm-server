
package fm.comunas.ddd.session.adapter.rest;

import fm.comunas.ddd.oe.model.ObjUserRepository;
import fm.comunas.ddd.session.adapter.rest.dto.LoginRequest;
import fm.comunas.ddd.session.adapter.rest.dto.LoginResponse;
import fm.comunas.ddd.session.adapter.rest.dto.SessionContextRequest;
import fm.comunas.ddd.session.adapter.rest.dto.SessionInfoReponse;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.ddd.session.model.impl.UserDetailsImpl;
import fm.comunas.ddd.session.service.api.JwtProvider;
import fm.comunas.ddd.session.service.api.SessionService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController("sessionController")
@RequestMapping("/api/session")
public class SessionController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	ObjUserRepository userRepository;

	@Autowired
	JwtProvider jwtProvider;

	@Autowired
	SessionInfo sessionInfo;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		Authentication authToken = this.getAuthToken(loginRequest);
		Authentication authentication = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.getJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();
		//@formatter:off
		return ResponseEntity.ok(
			LoginResponse.builder()
				.token(jwt)
				.id(userDetails.getId())
				.username(userDetails.getUsername())
				.email(userDetails.getEmail())
				.roles(roles)
				//.customValues(sessionInfo.getCustomValues())
				.build()
		);
		//@formatter:on
	}

	private Authentication getAuthToken(LoginRequest loginRequest) {
		return new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
	}

	@PostMapping("/session")
	public ResponseEntity<SessionInfoReponse> setSessionContext(@RequestBody SessionContextRequest contextRequest) {
		if (this.sessionInfo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		sessionInfo.clearCustomValues();
		if (contextRequest.getCustomValues() != null) {
			for (String key : contextRequest.getCustomValues().keySet()) {
				sessionInfo.setCustomValue(key, contextRequest.getCustomValues().get(key));
			}
		}
		return ResponseEntity.ok(SessionInfoReponse.fromSession(sessionInfo));
	}

	@GetMapping("/session")
	public ResponseEntity<SessionInfoReponse> getSessionInfo() {
		if (this.sessionInfo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		return ResponseEntity.ok(SessionInfoReponse.fromSession(sessionInfo));
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
