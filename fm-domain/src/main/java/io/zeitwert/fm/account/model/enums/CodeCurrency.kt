package io.zeitwert.fm.account.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Currency enum using the NEW dddrive framework.
 *
 * Values derived from: db/V1.0/3-config/R__1011_account_config.sql
 */
enum class CodeCurrency(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    CHF("chf", "CHF"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeCurrency>(CodeCurrency::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getCurrency(itemId: String): CodeCurrency? = getItem(itemId)
    }
}

