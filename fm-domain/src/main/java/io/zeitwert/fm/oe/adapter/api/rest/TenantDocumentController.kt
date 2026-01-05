package io.zeitwert.fm.oe.adapter.api.rest

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController
import io.zeitwert.fm.oe.model.ObjTenantRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("tenantDocumentController")
@RequestMapping("/rest/oe/tenants")
class TenantDocumentController {

	@Autowired
	var sessionContext: SessionContext? = null

	@Autowired
	lateinit var tenantRepository: ObjTenantRepository

	@Autowired
	lateinit var documentController: DocumentContentController

	@GetMapping(value = ["/{id}/logo"])
	fun getImage(
		@PathVariable id: Int,
	): ResponseEntity<ByteArray> {
		val tenant = this.tenantRepository.get(id)
		val documentId = tenant.logoImageId as Int?
		if (documentId == null) {
			return ResponseEntity.noContent().build()
		}
		return this.documentController.getContent(documentId)
	}

}
