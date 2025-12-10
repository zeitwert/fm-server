package io.zeitwert.fm.contact.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Salutation enum using the NEW dddrive framework.
 */
enum class CodeSalutation(
    private val id: String,
    private val itemName: String,
    val genderId: String,
) : Enumerated {
    MR("mr", "Herr", "male"),
    MRS("mrs", "Frau", "female"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeSalutation>(CodeSalutation::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getSalutation(itemId: String): CodeSalutation? = getItem(itemId)
    }
}

