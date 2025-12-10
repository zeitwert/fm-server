package io.zeitwert.fm.contact.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Gender enum using the NEW dddrive framework.
 */
enum class CodeGender(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    MALE("male", "Mann"),
    FEMALE("female", "Frau"),
    OTHER("other", "Andere"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeGender>(CodeGender::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getGender(itemId: String): CodeGender? = getItem(itemId)
    }
}

