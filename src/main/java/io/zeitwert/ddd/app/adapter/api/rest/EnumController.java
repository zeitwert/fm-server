
package io.zeitwert.ddd.app.adapter.api.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.oe.service.api.TenantService;
import io.zeitwert.ddd.oe.service.api.UserService;

@RestController("enumController")
@RequestMapping("/enum")
public class EnumController {

	@Autowired
	Enumerations enumerations;

	@Autowired
	ObjTenantRepository tenantRepo;

	@Autowired
	TenantService tenantService;

	@Autowired
	ObjUserRepository userRepo;

	@Autowired
	UserService userService;

	@GetMapping("/oe/objTenant")
	public ResponseEntity<List<EnumeratedDto>> getTenants() {
		QuerySpec querySpec = new QuerySpec(ObjUser.class);
		List<ObjTenantVRecord> tenantList = tenantRepo.find(querySpec);
		return ResponseEntity.ok(tenantList.stream()
				.map(tenant -> EnumeratedDto.builder().id(tenant.getId().toString()).name(tenant.getName()).build()).toList());
	}

	@GetMapping("/oe/objTenant/{idOrExtlKey}")
	public ResponseEntity<ObjTenant> getTenant(@PathVariable String idOrExtlKey) {
		Optional<ObjTenant> tenant = tenantService.getByExtlKey(idOrExtlKey);
		if (tenant.isEmpty()) {
			tenant = Optional.of(tenantService.getTenant(Integer.valueOf(idOrExtlKey)));
		}
		if (tenant.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(tenant.get());
	}

	@GetMapping("/oe/objUser")
	public ResponseEntity<List<EnumeratedDto>> getUsers() {
		QuerySpec querySpec = new QuerySpec(ObjUser.class);
		List<ObjUserVRecord> userList = userRepo.find(querySpec);
		return ResponseEntity.ok(userList.stream()
				.map(user -> EnumeratedDto.builder().id(user.getId().toString()).name(user.getName()).build()).toList());
	}

	@GetMapping("/oe/objUser/{idOrEmail}")
	public ResponseEntity<ObjUser> getUser(@PathVariable String idOrEmail) {
		Optional<ObjUser> user = userService.getByEmail(idOrEmail);
		if (user.isEmpty()) {
			user = Optional.of(userService.getUser(Integer.valueOf(idOrEmail)));
		}
		if (user.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user.get());
	}

	@GetMapping("/doc/codeCaseStage/{caseDef}")
	public ResponseEntity<List<CodeCaseStage>> getCaseStageDomain(@PathVariable String caseDef) {
		CodeCaseStageEnum enumeration = this.enumerations.getEnumeration(CodeCaseStageEnum.class);
		if (enumeration == null) {
			return ResponseEntity.notFound().build();
		}
		List<CodeCaseStage> itemList = enumeration.getItems().stream()
				.filter((item) -> item.getId().startsWith(caseDef + ".") && !item.getCaseStageTypeId().equals("abstract"))
				.toList();
		return ResponseEntity.ok(itemList);
	}

	@GetMapping("/{module}/{enumerationName}")
	public ResponseEntity<List<? extends Enumerated>> getEnumDomain(@PathVariable String module,
			@PathVariable String enumerationName, @RequestParam(required = false, defaultValue = "") List<String> filter) {
		Enumeration<? extends Enumerated> enumeration = this.enumerations.getEnumeration(module, enumerationName);
		if (enumeration == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(enumeration.getItems().stream().filter(i -> !filter.contains(i.getId())).toList());
	}

	@GetMapping("/{module}/{enumerationName}/{id}")
	@SuppressWarnings("unchecked")
	public ResponseEntity<Enumerated> getEnumItem(@PathVariable String module, @PathVariable String enumerationName,
			@PathVariable String id) {
		Enumerated item = ((Enumeration<Enumerated>) this.enumerations.getEnumeration(module, enumerationName)).getItem(id);
		if (item == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(item);
	}

}
