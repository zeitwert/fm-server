package io.zeitwert.fm.app.adapter.api.rest;

import dddrive.app.doc.model.enums.CodeCaseStage;
import dddrive.app.doc.model.enums.CodeCaseStageEnum;
import dddrive.ddd.core.model.RepositoryDirectory;
import dddrive.ddd.enums.model.Enumerated;
import dddrive.ddd.enums.model.Enumeration;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.oe.model.ObjTenant;
import io.zeitwert.fm.oe.model.ObjTenantRepository;
import io.zeitwert.fm.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController("enumController")
@RequestMapping("/enum")
public class EnumController {

	@Autowired
	RequestContextFM requestContext;

	@Autowired
	RepositoryDirectory directory;

	@Autowired
	ObjTenantRepository tenantRepo;

	@Autowired
	ObjUserRepository userRepo;

//	@GetMapping("/oe/objTenant")
//	public ResponseEntity<List<EnumeratedDto>> getTenants() {
//		QuerySpec querySpec = new QuerySpec(ObjUser.class);
//		List<ObjTenantVRecord> tenants = this.tenantRepo.find(querySpec);
//		return ResponseEntity.ok(
//				tenants.stream()
//						.map(obj -> (TableRecord<?>) obj)
//						.map(tenant -> EnumeratedDto.builder().id(tenant.get(ObjFields.ID).toString())
//								.name(tenant.get(ObjFields.CAPTION)).build())
//						.toList());
//	}

	@GetMapping("/oe/objTenant/{id}")
	public ResponseEntity<ObjTenant> getTenant(@PathVariable Integer id) {
		ObjTenant tenant = this.tenantRepo.get(id);
		return ResponseEntity.ok(tenant);
	}

	@GetMapping("/oe/objUser")
	public ResponseEntity<List<EnumeratedDto>> getUsers() {
		Object tenantId = requestContext.getTenantId();
		List<ObjUser> users = userRepo.find(null).stream().map(it -> userRepo.get(it)).toList();
		return ResponseEntity.ok(users.stream().map(u -> EnumeratedDto.of(u.getId().toString(), u.getCaption())).toList());
	}

	@GetMapping("/oe/objUser/{idOrEmail}")
	public ResponseEntity<ObjUser> getUser(@PathVariable String idOrEmail) {
		Optional<ObjUser> user = this.userRepo.getByEmail(idOrEmail);
		if (user.isEmpty()) {
			user = Optional.of(this.userRepo.get(Integer.valueOf(idOrEmail)));
		}
		if (user.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user.get());
	}

	@GetMapping("/doc/codeCaseStage/{caseDef}")
	public ResponseEntity<List<CodeCaseStage>> getCaseStageDomain(@PathVariable String caseDef) {
		List<CodeCaseStage> itemList = CodeCaseStageEnum.instance.getItems().stream()
				.filter((item) -> item.getId().startsWith(caseDef + ".") && !"abstract".equals(item.getCaseStageTypeId()))
				.toList();
		return ResponseEntity.ok(itemList);
	}

	@GetMapping("/{module}/{enumerationName}")
	public ResponseEntity<List<? extends Enumerated>> getEnumDomain(@PathVariable String module,
																																	@PathVariable String enumerationName, @RequestParam(required = false, defaultValue = "") List<String> filter) {
		Enumeration<? extends Enumerated> enumeration = directory.getEnumeration(module, enumerationName);
		if (enumeration == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(enumeration.getItems().stream().filter(i -> !filter.contains(i.getId())).toList());
	}

	@GetMapping("/{module}/{enumerationName}/{id}")
	@SuppressWarnings("unchecked")
	public ResponseEntity<Enumerated> getEnumItem(@PathVariable String module, @PathVariable String enumerationName,
																								@PathVariable String id) {
		Enumerated item = ((Enumeration<Enumerated>) directory.getEnumeration(module, enumerationName)).getItem(id);
		if (item == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(item);
	}

}
