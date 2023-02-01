
package io.zeitwert.fm.app.adapter.api.rest;

import java.util.List;
import java.util.Optional;

import org.jooq.TableRecord;
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
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;

@RestController("enumController")
@RequestMapping("/enum")
public class EnumController {

	@Autowired
	Enumerations enumerations;

	@Autowired
	ObjTenantRepository tenantRepo;

	@Autowired
	ObjTenantCache tenantCache;

	@Autowired
	ObjUserRepository userRepo;

	@Autowired
	ObjUserCache userCache;

	@GetMapping("/oe/objTenant")
	public ResponseEntity<List<EnumeratedDto>> getTenants() {
		QuerySpec querySpec = new QuerySpec(ObjUser.class);
		List<TableRecord<?>> tenants = this.tenantRepo.find(querySpec);
		return ResponseEntity.ok(
				tenants.stream()
						.map(tenant -> EnumeratedDto.builder().id(tenant.get(ObjFields.ID).toString())
								.name(tenant.get(ObjFields.CAPTION)).build())
						.toList());
	}

	@GetMapping("/oe/objTenant/{idOrExtlKey}")
	public ResponseEntity<ObjTenant> getTenant(@PathVariable String idOrExtlKey) {
		Optional<ObjTenant> tenant = this.tenantCache.getByExtlKey(idOrExtlKey);
		if (tenant.isEmpty()) {
			tenant = Optional.of(this.tenantCache.get(Integer.valueOf(idOrExtlKey)));
		}
		if (tenant.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(tenant.get());
	}

	@GetMapping("/oe/objUser")
	public ResponseEntity<List<EnumeratedDto>> getUsers() {
		QuerySpec querySpec = new QuerySpec(ObjUser.class);
		List<TableRecord<?>> users = this.userRepo.find(querySpec);
		return ResponseEntity.ok(
				users.stream()
						.map(tenant -> EnumeratedDto.builder().id(tenant.get(ObjFields.ID).toString())
								.name(tenant.get(ObjFields.CAPTION)).build())
						.toList());
	}

	@GetMapping("/oe/objUser/{idOrEmail}")
	public ResponseEntity<ObjUser> getUser(@PathVariable String idOrEmail) {
		Optional<ObjUser> user = this.userCache.getByEmail(idOrEmail);
		if (user.isEmpty()) {
			user = Optional.of(this.userCache.get(Integer.valueOf(idOrEmail)));
		}
		if (user.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user.get());
	}

	@GetMapping("/doc/codeCaseStage/{caseDef}")
	public ResponseEntity<List<CodeCaseStage>> getCaseStageDomain(@PathVariable String caseDef) {
		CodeCaseStageEnum enumeration = this.enumerations.getEnumerationByEnumeration(CodeCaseStageEnum.class);
		if (enumeration == null) {
			return ResponseEntity.notFound().build();
		}
		List<CodeCaseStage> itemList = enumeration.getItems().stream()
				.filter((item) -> item.getId().startsWith(caseDef + ".") && !"abstract".equals(item.getCaseStageTypeId()))
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
