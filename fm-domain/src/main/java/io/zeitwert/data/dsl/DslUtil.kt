package io.zeitwert.data.dsl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.data.DataSetup
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.enums.CodeContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

object DslUtil {

	val logger: Logger = LoggerFactory.getLogger(DataSetup::class.java)

	lateinit var directory: RepositoryDirectory

	val documentRepository: ObjDocumentRepository
		get() = directory.getRepository(ObjDocument::class.java) as ObjDocumentRepository

	fun init(directory: RepositoryDirectory) {
		this.directory = directory
	}

	/**
	 * Pretty-print indent string for logging.
	 */
	var indent = ""

	fun startIndent() {
		indent = ""
	}

	fun indent() {
		indent = if (indent.startsWith(".")) "$indent  " else ". "
	}

	fun outdent() {
		indent = if (indent.length <= 2) "" else indent.substring(0, indent.length - 2)
	}

	/**
	 * Upload logo content from classpath resource to a document.
	 * Loads the document for writing by ID.
	 *
	 * @param documentId The ID of the logo document to upload content to
	 * @param resourcePath The resource path relative to resources (e.g., "demo/tenant/logo-demo.png")
	 * @param userId The user ID for the upload
	 */
	fun uploadLogoFromResource(
		documentId: Any,
		resourcePath: String,
		userId: Any,
	) {
		val fullPath = "/$resourcePath"
		val inputStream = Tenant::class.java.getResourceAsStream(fullPath)
		if (inputStream == null) {
			logger.warn("${indent}No logo resource found at $resourcePath")
			return
		}

		val extension = resourcePath.substringAfterLast(".", "")
		val contentType = CodeContentType.getItemByExtension(extension)
		if (contentType == null) {
			logger.warn("${indent}Unknown content type for extension '$extension' in $resourcePath")
			return
		}

		val bytes = inputStream.use { it.readBytes() }
		// Load document for writing
		val document = documentRepository.load(documentId)
		document.storeContent(contentType, bytes, userId, OffsetDateTime.now())
		logger.info("${indent}Uploaded logo from $resourcePath")
	}

}
