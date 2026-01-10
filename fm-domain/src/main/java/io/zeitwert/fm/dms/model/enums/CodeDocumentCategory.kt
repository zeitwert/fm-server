package io.zeitwert.fm.dms.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

enum class CodeDocumentCategory(
	override val defaultName: String,
) : EnumeratedEnum {

	AVATAR("Avatar"),
	BANNER("Banner"),
	FOTO("Foto"),
	LOGO("Logo"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration :
		EnumerationBase<CodeDocumentCategory>(CodeDocumentCategory::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getDocumentCategory(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
