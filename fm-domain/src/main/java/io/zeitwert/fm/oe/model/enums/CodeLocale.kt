package io.zeitwert.fm.oe.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Locale enum using the NEW dddrive framework.
 */
enum class CodeLocale(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    EN_US("en-US", "English US"),
    EN_UK("en-UK", "English UK"),
    DE_CH("de-CH", "German CH"),
    DE_DE("de-DE", "German DE"),
    FR_CH("fr-CH", "French CH"),
    FR_FR("fr-FR", "French FR"),
    ES_ES("es-ES", "Spanish ES"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeLocale>(CodeLocale::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getLocale(itemId: String): CodeLocale? = getItem(itemId)
    }
}

