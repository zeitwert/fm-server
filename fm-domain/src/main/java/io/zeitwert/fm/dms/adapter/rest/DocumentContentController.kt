package io.zeitwert.fm.dms.adapter.rest

import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.enums.CodeContentType.Enumeration.getContentType
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController("documentContentController")
@RequestMapping("/rest/dms/documents")
class DocumentContentController(
	private val documentRepository: ObjDocumentRepository,
	private val sessionContext: SessionContext,
) {

	@Value("classpath:templates/missing.jpg")
	lateinit var missingImage: Resource

	@GetMapping(value = ["/{documentId}/content"])
	fun getContent(
		@PathVariable documentId: Int,
	): ResponseEntity<ByteArray> {
		val document = documentRepository.get(documentId)
		val contentType = document.contentType
		val content = if (contentType == null) {
			missingImage.inputStream.readAllBytes()
		} else {
			document.content
		}
		val contentDisposition = ContentDisposition.builder("inline").filename(document.name).build()
		val headers = HttpHeaders()
		headers.contentDisposition = contentDisposition
		return ResponseEntity
			.ok()
			.contentType(contentType?.getMediaType() ?: MediaType.APPLICATION_OCTET_STREAM)
			.headers(headers)
			.body(content)
	}

	@PostMapping(value = ["/{documentId}/content"])
	fun setContent(
		@PathVariable documentId: Int,
		@RequestParam("file") file: MultipartFile,
	): ResponseEntity<Void> {
		try {
			val contentType = getContentType(
				file.contentType,
				file.originalFilename,
			)
			if (contentType == null) {
				return ResponseEntity.badRequest().body(null)
			}
			val document = documentRepository.load(documentId)
			document.storeContent(contentType, file.bytes, sessionContext.userId, sessionContext.currentTime)
		} catch (e: IOException) {
			e.printStackTrace()
			return ResponseEntity.internalServerError().body(null)
		}
		return ResponseEntity.ok().body(null)
	}

}
