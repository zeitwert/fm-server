package io.zeitwert.app.api.rest

import dddrive.app.doc.model.enums.CodeCaseStageEnum
import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("enumController")
@RequestMapping("/enum")
class EnumController {

	@Autowired
	lateinit var sessionContext: SessionContext

	@Autowired
	lateinit var directory: RepositoryDirectory

	@Autowired
	lateinit var tenantRepo: ObjTenantRepository

	@Autowired
	lateinit var userRepo: ObjUserRepository

	// 	@GetMapping("/oe/objTenant")
	// 	public ResponseEntity<List<EnumeratedDto>> getTenants() {
	// 		QuerySpec querySpec = new QuerySpec(ObjUser.class);
	// 		List<ObjTenantVRecord> tenants = tenantRepo.find(querySpec);
	// 		return ResponseEntity.ok(
	// 				tenants.stream()
	// 						.map(obj -> (TableRecord<?>) obj)
	// 						.map(tenant -> EnumeratedDto.builder().id(tenant.get(ObjFields.ID).toString())
	// 								.name(tenant.get(ObjFields.CAPTION)).build())
	// 						.toList());
	// 	}

	@GetMapping("/oe/objTenant/{id}")
	fun getTenant(
		@PathVariable id: Int,
	): ResponseEntity<ObjTenant> {
		val tenant = tenantRepo.get(id)
		return ResponseEntity.ok(tenant)
	}

	@GetMapping("/oe/objUser")
	fun getUsers(): ResponseEntity<List<EnumeratedDto>> {
		sessionContext.tenantId
		val users = userRepo.find(null).map { userRepo.get(it) }
		return ResponseEntity.ok(users.map { EnumeratedDto.of(it.id.toString(), it.caption) })
	}

	@GetMapping("/oe/objUser/{idOrEmail}")
	fun getUser(
		@PathVariable idOrEmail: String,
	): ResponseEntity<ObjUser> {
		try {
			val maybeUser = userRepo.getByEmail(idOrEmail)
			if (maybeUser.isPresent) {
				return ResponseEntity.ok(maybeUser.get())
			}
			val user = userRepo.get(idOrEmail.toInt())
			return ResponseEntity.ok(user)
		} catch (e: Exception) {
			return ResponseEntity.notFound().build()
		}
	}

	@GetMapping("/doc/codeCaseStage/{caseDef}")
	fun getCaseStageDomain(
		@PathVariable caseDef: String,
	): ResponseEntity<List<EnumeratedDto>> {
		val stages = CodeCaseStageEnum.instance.items
			.filter { it.id.startsWith("$caseDef.") && "abstract" != it.caseStageTypeId }
		return ResponseEntity.ok(stages.map { EnumeratedDto.of(it)!! })
	}

	@GetMapping("/{module}/{enumerationName}")
	fun getEnumDomain(
		@PathVariable module: String,
		@PathVariable enumerationName: String,
		@RequestParam(required = false, defaultValue = "") filter: List<String>,
	): ResponseEntity<List<EnumeratedDto>> {
		try {
			val enumeration = directory.getEnumeration(module, enumerationName)
			val items = enumeration.items.filter { !filter.contains(it.id) }.map { EnumeratedDto.of(it)!! }
			return ResponseEntity.ok().body(items)
		} catch (e: Exception) {
			return ResponseEntity.notFound().build()
		}
	}

	@GetMapping("/{module}/{enumerationName}/{id}")
	fun getEnumItem(
		@PathVariable module: String,
		@PathVariable enumerationName: String,
		@PathVariable id: String,
	): ResponseEntity<EnumeratedDto> {
		try {
			val item = directory.getEnumeration(module, enumerationName).getItem(id)
			return ResponseEntity.ok(EnumeratedDto.of(item)!!)
		} catch (e: Exception) {
			return ResponseEntity.notFound().build()
		}
	}

}
