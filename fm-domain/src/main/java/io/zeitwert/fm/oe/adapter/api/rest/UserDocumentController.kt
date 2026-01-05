package io.zeitwert.fm.oe.adapter.api.rest

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("userDocumentController")
@RequestMapping("/rest/oe/users")
class UserDocumentController {

	@Autowired
	var sessionContext: SessionContext? = null

	@Autowired
	lateinit var userRepository: ObjUserRepository

	@Autowired
	lateinit var documentController: DocumentContentController

	@GetMapping(value = ["/{id}/avatar"])
	fun getAvatar(
		@PathVariable id: Int,
	): ResponseEntity<ByteArray> {
		val user = this.userRepository.get(id)
		val documentId = user.avatarImageId
		if (documentId == null) {
			val headers = HttpHeaders()
			headers.add("Location", "/default-user.png")
			return ResponseEntity(headers, HttpStatus.FOUND)
		}
		return this.documentController.getContent(documentId as Int)
	}

}
