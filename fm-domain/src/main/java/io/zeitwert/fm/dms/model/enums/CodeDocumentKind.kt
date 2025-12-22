package io.zeitwert.fm.dms.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeDocumentKind(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	STANDALONE("standalone", "Standalone"),
	TEMPLATE("template", "Template"),
	INSTANCE("instance", "Instance"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeDocumentKind>(CodeDocumentKind::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getDocumentKind(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
