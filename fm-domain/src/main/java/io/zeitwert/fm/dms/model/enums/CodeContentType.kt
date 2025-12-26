package io.zeitwert.fm.dms.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase
import org.springframework.http.MediaType

enum class CodeContentType(
	override val defaultName: String,
	val contentKind: CodeContentKind,
	val extension: String,
	val mimeType: String,
) : EnumeratedEnum {

	// @formatter:off
	PDF("PDF", CodeContentKind.DOCUMENT, "pdf", "application/pdf"),
	DOC("Word", CodeContentKind.DOCUMENT, "doc", "application/msword"),
	XLS("Excel", CodeContentKind.DOCUMENT, "xls", "application/vnd.ms-excel"),
	PPT("Powerpoint", CodeContentKind.DOCUMENT, "ppt", "application/vnd.ms-powerpoint"),
	DOCX("Word 2007", CodeContentKind.DOCUMENT, "docx", "application/vnd.openxmlformats-officedocument.wordprocessing"),
	XLSX("Excel 2007", CodeContentKind.DOCUMENT, "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	PPTX("Powerpoint 2007", CodeContentKind.DOCUMENT, "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
	JPG("JPG Image", CodeContentKind.FOTO, "jpg", "image/jpeg"),
	GIF("GIF Image", CodeContentKind.FOTO, "gif", "image/gif"),
	PNG("PNG Image", CodeContentKind.FOTO, "png", "image/png"),
	SVG("SVG Image", CodeContentKind.FOTO, "svg", "image/svg+xml"),
	MPEG("MPEG Video", CodeContentKind.VIDEO, "mpeg", "video/mpeg"),
	MP4("MP4 Video", CodeContentKind.VIDEO, "mp4", "video/mp4"),
	MOV("MOV Video", CodeContentKind.VIDEO, "mov", "video/quicktime"),
	AVI("AVI Video", CodeContentKind.VIDEO, "avi", "video/avi"),
	MP3("MP3 Audio", CodeContentKind.VIDEO, "mp3", "audio/mpeg"),
	;

	// @formatter:on

	override val enumeration get() = Enumeration

	fun getMediaType(): MediaType = MediaType.parseMediaType(mimeType)

	companion object Enumeration : EnumerationBase<CodeContentType>(CodeContentType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getContentType(itemId: String?) = if (itemId != null) getItem(itemId) else null

		@JvmStatic
		fun getContentType(
			mimeType: String?,
			fileName: String?,
		): CodeContentType? {
			if (!mimeType.isNullOrEmpty()) {
				getItemByMimeType(mimeType)?.let { return it }
			}
			if (!fileName.isNullOrEmpty() && fileName.contains(".")) {
				val extension = fileName.substringAfterLast(".")
				getItemByExtension(extension)?.let { return it }
			}
			return null
		}

		@JvmStatic
		fun getItemByMimeType(mimeType: String): CodeContentType? = entries.find { it.mimeType.equals(mimeType, ignoreCase = true) }

		@JvmStatic
		fun getItemByExtension(extension: String): CodeContentType? = entries.find { it.extension.equals(extension, ignoreCase = true) }

		@JvmStatic
		fun getContentTypes(contentKind: CodeContentKind): List<CodeContentType> = entries.filter { it.contentKind == contentKind }
	}
}
