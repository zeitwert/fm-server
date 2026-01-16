package io.zeitwert.app.api.rest

import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.app.api.rest.dto.TenantInfoResponse
import io.zeitwert.app.api.rest.dto.UserInfoResponse
import io.zeitwert.fm.account.api.AccountService
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("fmApplicationController")
@RequestMapping("/rest/app")
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "sql", matchIfMissing = true)
class ApplicationController {

	@Autowired
	lateinit var tenantRepository: ObjTenantRepository

	@Autowired
	lateinit var userRepository: ObjUserRepository

	@Autowired
	lateinit var accountService: AccountService

	@GetMapping("/userInfo/{email}")
	fun userInfo(
		@PathVariable email: String,
	): ResponseEntity<UserInfoResponse> {
		try {
			val user = userRepository.getByEmail(email).get()
			return ResponseEntity.ok(
				UserInfoResponse(
					id = user.id as Int,
					email = user.email!!,
					name = user.name!!,
					role = EnumeratedDto.of(user.role)!!,
					tenants = user.tenantSet.map { EnumeratedDto.of(tenantRepository.get(it))!! },
				),
			)
		} catch (e: NumberFormatException) {
			return ResponseEntity.notFound().build()
		}
	}

	@GetMapping("/tenantInfo/{id}")
	fun tenantInfo(
		@PathVariable id: Int,
	): ResponseEntity<TenantInfoResponse> {
		try {
			val tenant = tenantRepository.get(id)
			val accounts = accountService.getAccounts(tenant)
			val accountDtos = accounts.map { EnumeratedDto.of(it.id.toString(), it.name) }
			return ResponseEntity.ok(
				TenantInfoResponse(
					id = id,
					tenantType = EnumeratedDto.of(tenant.tenantType)!!,
					accounts = accountDtos,
				),
			)
		} catch (e: NumberFormatException) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
		}
	}

}
