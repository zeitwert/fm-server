package io.zeitwert.fm.dms.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeContentKind(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	DOCUMENT("document", "Document"),
	FOTO("foto", "Foto"),
	VIDEO("video", "Video"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	fun getContentTypes(): List<CodeContentType> = CodeContentType.entries.filter { it.contentKind == this }

	fun getExtensions(): List<String> = getContentTypes().map { ".${it.extension}" }

	companion object Enumeration : EnumerationBase<CodeContentKind>(CodeContentKind::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getContentKind(itemId: String): CodeContentKind? = getItem(itemId)
	}
}
