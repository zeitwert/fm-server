package fm.comunas.fm.app.adapter.api.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.oe.model.ObjUserRepository;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.fm.account.model.db.tables.records.ObjAccountVRecord;
import fm.comunas.fm.account.service.api.AccountService;
import fm.comunas.fm.app.adapter.api.rest.dto.UserInfoResponse;

@RestController("fmApplicationController")
@RequestMapping("/api/app")
public class ApplicationController {

	@Autowired
	SessionInfo sessionInfo;

	@Autowired
	ObjUserRepository userRepository;

	@Autowired
	AccountService accountService;

	@GetMapping("/userInfo/{email}")
	public ResponseEntity<UserInfoResponse> userInfo(@PathVariable("email") String email) {
		Optional<ObjUser> maybeUser = this.userRepository.getByEmail(email);
		if (!maybeUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		ObjUser user = maybeUser.get();
		ObjTenant tenant = user.getTenant();
		List<ObjAccountVRecord> communities = this.accountService.getAccountList(sessionInfo, tenant);
		List<EnumeratedDto> communitiesDto = communities.stream()
				.map(hh -> EnumeratedDto.builder().id(hh.getId().toString()).name(hh.getName()).build()).toList();
		//@formatter:off
		return ResponseEntity.ok(
			UserInfoResponse.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.roles(user.getRoleList().stream().map(r -> r.getId()).toList())
				.communities(communitiesDto)
				.build()
		);
		//@formatter:on
	}

}
