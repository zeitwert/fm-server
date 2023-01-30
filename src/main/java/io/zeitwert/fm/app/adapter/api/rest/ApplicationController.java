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
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.service.api.AccountService;
import io.zeitwert.fm.app.ApplicationService;
import io.zeitwert.fm.app.adapter.api.rest.dto.TenantInfoResponse;
import io.zeitwert.fm.app.adapter.api.rest.dto.UserInfoResponse;
import io.zeitwert.fm.app.model.Application;
import io.zeitwert.fm.app.model.ApplicationInfo;

@RestController("fmApplicationController")
@RequestMapping("/rest/app")
public class ApplicationController {

	@Autowired
	private ObjTenantCache tenantCache;

	@Autowired
	private ObjUserCache userCache;

	@Autowired
	private AccountService accountService;

	@Autowired
	private ApplicationService applicationService;

	@GetMapping("/applications")
	ResponseEntity<List<Application>> getApplications() {
		return ResponseEntity.ok(this.applicationService.getAllApplications());
	}

	@GetMapping("/applications/{id}")
	ResponseEntity<ApplicationInfo> getApplication(@PathVariable String id) {
		ApplicationInfo appInfo = this.applicationService.getApplicationMenu(id);
		if (appInfo == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(appInfo);
	}

	@GetMapping("/userInfo/{email}")
	public ResponseEntity<UserInfoResponse> userInfo(@PathVariable("email") String email) {
		Optional<ObjUser> maybeUser = this.userCache.getByEmail(email);
		if (!maybeUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		ObjUser user = maybeUser.get();
		//@formatter:off
		return ResponseEntity.ok(
			UserInfoResponse.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.role(EnumeratedDto.fromEnum(user.getRole()))
				.tenants(user.getTenantSet().stream().map(t -> this.tenantCache.getAsEnumerated(t.getId())).toList())
				.build()
		);
		//@formatter:on
	}

	@GetMapping("/tenantInfo/{id}")
	public ResponseEntity<TenantInfoResponse> tenantInfo(@PathVariable("id") Integer id) {
		ObjTenant tenant = this.tenantCache.get(id);
		List<ObjAccountVRecord> accounts = this.accountService.getAccounts(tenant);
		List<EnumeratedDto> accountDtos = accounts.stream()
				.map(account -> EnumeratedDto.builder().id(account.getId().toString()).name(account.getName()).build())
				.toList();
		//@formatter:off
		return ResponseEntity.ok(
			TenantInfoResponse.builder()
				.id(id)
				.tenantType(EnumeratedDto.fromEnum(tenant.getTenantType()))
				.accounts(accountDtos)
				.build()
		);
		//@formatter:on
	}

}
