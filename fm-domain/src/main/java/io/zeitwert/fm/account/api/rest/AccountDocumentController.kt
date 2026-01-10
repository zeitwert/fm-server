package io.zeitwert.fm.account.api.rest

import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.dms.api.rest.DocumentContentController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("accountDocumentController")
@RequestMapping("/rest/account/accounts")
class AccountDocumentController {

	@Autowired
	var sessionContext: SessionContext? = null

	@Autowired
	lateinit var accountRepository: ObjAccountRepository

	@Autowired
	lateinit var documentController: DocumentContentController

	@GetMapping(value = ["/{id}/logo"])
	fun getImage(
		@PathVariable id: Int,
	): ResponseEntity<ByteArray> {
		val account = this.accountRepository.get(id)
		val documentId = account.logoImageId as Int?
		if (documentId == null) {
			return ResponseEntity.noContent().build()
		}
		return this.documentController.getContent(documentId)
	}

}
