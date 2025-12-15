package io.zeitwert.fm.test.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Test type enum using the NEW dddrive framework.
 * Used for testing enum properties without disturbing other enums like CodeCountry.
 */
enum class CodeTestType(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	TYPE_A("type_a", "Type A"),
	TYPE_B("type_b", "Type B"),
	TYPE_C("type_c", "Type C"),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTestType>(CodeTestType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getTestType(itemId: String): CodeTestType? = getItem(itemId)
	}
}
