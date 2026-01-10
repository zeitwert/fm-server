package io.zeitwert.fm.dms.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

enum class CodeDocumentKind(
	override val defaultName: String,
) : EnumeratedEnum {

	STANDALONE("Standalone"),
	TEMPLATE("Template"),
	INSTANCE("Instance"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeDocumentKind>(CodeDocumentKind::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getDocumentKind(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
