package io.zeitwert.fm.dms.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeDocumentKind(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	STANDALONE("standalone", "Standalone"),
	TEMPLATE("template", "Template"),
	INSTANCE("instance", "Instance"),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeDocumentKind>(CodeDocumentKind::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getDocumentKind(itemId: String): CodeDocumentKind? = getItem(itemId)
	}
}
