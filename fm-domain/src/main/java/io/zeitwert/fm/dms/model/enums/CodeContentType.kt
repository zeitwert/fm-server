package io.zeitwert.fm.dms.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase
import org.springframework.http.MediaType

enum class CodeContentType(
	override val id: String,
	private val itemName: String,
	val contentKind: CodeContentKind,
	val extension: String,
	val mimeType: String,
) : Enumerated {

	PDF("pdf", "PDF", CodeContentKind.DOCUMENT, "pdf", "application/pdf"),
	DOC("doc", "Word", CodeContentKind.DOCUMENT, "doc", "application/msword"),
	XLS("xls", "Excel", CodeContentKind.DOCUMENT, "xls", "application/vnd.ms-excel"),
	PPT("ppt", "Powerpoint", CodeContentKind.DOCUMENT, "ppt", "application/vnd.ms-powerpoint"),
	DOCX(
		"docx",
		"Word 2007",
		CodeContentKind.DOCUMENT,
		"docx",
		"application/vnd.openxmlformats-officedocument.wordprocessing",
	),
	XLSX(
		"xlsx",
		"Excel 2007",
		CodeContentKind.DOCUMENT,
		"xlsx",
		"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
	),
	PPTX(
		"pptx",
		"Powerpoint 2007",
		CodeContentKind.DOCUMENT,
		"pptx",
		"application/vnd.openxmlformats-officedocument.presentationml.presentation",
	),
	JPG("jpg", "JPG Image", CodeContentKind.FOTO, "jpg", "image/jpeg"),
	GIF("gif", "GIF Image", CodeContentKind.FOTO, "gif", "image/gif"),
	PNG("png", "PNG Image", CodeContentKind.FOTO, "png", "image/png"),
	SVG("svg", "SVG Image", CodeContentKind.FOTO, "svg", "image/svg+xml"),
	MPEG("mpeg", "MPEG Video", CodeContentKind.VIDEO, "mpeg", "video/mpeg"),
	MP4("mp4", "MP4 Video", CodeContentKind.VIDEO, "mp4", "video/mp4"),
	MOV("mov", "MOV Video", CodeContentKind.VIDEO, "mov", "video/quicktime"),
	AVI("avi", "AVI Video", CodeContentKind.VIDEO, "avi", "video/avi"),
	MP3("mp3", "MP3 Audio", CodeContentKind.VIDEO, "mp3", "audio/mpeg"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	fun getMediaType(): MediaType = MediaType.parseMediaType(mimeType)

	companion object Enumeration : EnumerationBase<CodeContentType>(CodeContentType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getContentType(itemId: String): CodeContentType? = getItem(itemId)

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
