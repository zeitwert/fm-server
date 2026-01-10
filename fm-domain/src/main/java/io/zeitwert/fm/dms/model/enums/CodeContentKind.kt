package io.zeitwert.fm.dms.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

enum class CodeContentKind(
	override val defaultName: String,
) : EnumeratedEnum {

	DOCUMENT("Document"),
	FOTO("Foto"),
	VIDEO("Video"),
	;

	override val enumeration get() = Enumeration

	fun getContentTypes(): List<CodeContentType> = CodeContentType.entries.filter { it.contentKind == this }

	fun getExtensions(): List<String> = getContentTypes().map { ".${it.extension}" }

	companion object Enumeration : EnumerationBase<CodeContentKind>(CodeContentKind::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getContentKind(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
