package io.zeitwert.fm.app.adapter.api.rest

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.account.service.api.AccountService
import io.zeitwert.fm.app.ApplicationService
import io.zeitwert.fm.app.adapter.api.rest.dto.TenantInfoResponse
import io.zeitwert.fm.app.adapter.api.rest.dto.UserInfoResponse
import io.zeitwert.fm.app.model.Application
import io.zeitwert.fm.app.model.ApplicationInfo
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("fmApplicationController")
@RequestMapping("/rest/app")
class ApplicationController {

	@Autowired
	lateinit var tenantRepository: ObjTenantRepository

	@Autowired
	lateinit var userRepository: ObjUserRepository

	@Autowired
	lateinit var accountService: AccountService

	@Autowired
	lateinit var applicationService: ApplicationService

	@GetMapping("/applications")
	fun getApplications(): ResponseEntity<List<Application>> = ResponseEntity.ok(applicationService.getAllApplications())

	@GetMapping("/applications/{id}")
	fun getApplication(
		@PathVariable id: String,
	): ResponseEntity<ApplicationInfo> {
		try {
			val appInfo = applicationService.getApplicationMenu(id)
			return ResponseEntity.ok(appInfo)
		} catch (e: NumberFormatException) {
			return ResponseEntity.notFound().build()
		}
	}

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
