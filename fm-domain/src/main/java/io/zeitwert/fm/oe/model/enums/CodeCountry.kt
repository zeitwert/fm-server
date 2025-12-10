package io.zeitwert.fm.oe.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Country enum using the NEW dddrive framework.
 */
enum class CodeCountry(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    CH("ch", "Schweiz"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeCountry>(CodeCountry::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getCountry(itemId: String): CodeCountry? = getItem(itemId)
    }
}

