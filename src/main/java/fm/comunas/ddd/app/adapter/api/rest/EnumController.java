
package fm.comunas.ddd.app.adapter.api.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.doc.model.enums.CodeCaseStage;
import fm.comunas.ddd.doc.model.enums.CodeCaseStageEnum;
import fm.comunas.ddd.enums.model.Enumerated;
import fm.comunas.ddd.enums.model.Enumeration;
import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.oe.model.ObjTenantRepository;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.oe.model.ObjUserRepository;

@RestController("enumController")
@RequestMapping("/enum")
public class EnumController {

	private final Enumerations enumerations;
	private final ObjTenantRepository tenantRepository;
	private final ObjUserRepository userRepository;

	@Autowired
	public EnumController(Enumerations enumerations, ObjTenantRepository tenantRepository,
			ObjUserRepository userRepository) {
		this.enumerations = enumerations;
		this.tenantRepository = tenantRepository;
		this.userRepository = userRepository;
	}

	@GetMapping("/oe/objTenant/{idOrExtlKey}")
	public ResponseEntity<ObjTenant> getTenant(@PathVariable String idOrExtlKey) {
		Optional<ObjTenant> tenant = tenantRepository.getByExtlKey(idOrExtlKey);
		if (tenant.isEmpty()) {
			tenant = tenantRepository.get(Integer.valueOf(idOrExtlKey));
		}
		if (tenant.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(tenant.get());
	}

	@GetMapping("/oe/objUser/{idOrEmail}")
	public ResponseEntity<ObjUser> getUser(@PathVariable String idOrEmail) {
		Optional<ObjUser> user = userRepository.getByEmail(idOrEmail);
		if (user.isEmpty()) {
			user = userRepository.get(Integer.valueOf(idOrEmail));
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
