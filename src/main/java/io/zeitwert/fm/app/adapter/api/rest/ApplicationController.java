package io.zeitwert.fm.app.adapter.api.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.service.api.AccountService;
import io.zeitwert.fm.app.adapter.api.rest.dto.UserInfoResponse;

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
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		Optional<ObjUser> maybeUser = this.userRepository.getByEmail(sessionInfo, email);
		if (!maybeUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		ObjUser user = maybeUser.get();
		List<ObjAccountVRecord> accounts = this.accountService.getAccountList(sessionInfo, user.getTenant());
		List<EnumeratedDto> accountsDto = accounts.stream()
				.map(account -> EnumeratedDto.builder().id(account.getId().toString()).name(account.getName()).build())
				.toList();
		//@formatter:off
		return ResponseEntity.ok(
			UserInfoResponse.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.tenant(tenantDtoAdapter.fromAggregate(user.getTenant(), sessionInfo))
				.role(user.getRole().getId())
				.accounts(accountsDto)
				.build()
		);
		//@formatter:on
	}

}
