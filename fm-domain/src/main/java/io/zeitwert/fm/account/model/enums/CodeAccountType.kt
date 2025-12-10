package io.zeitwert.fm.account.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Account type enum using the NEW dddrive framework.
 *
 * Values derived from: db/V1.0/3-config/R__1011_account_config.sql
 */
enum class CodeAccountType(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    PROSPECT("prospect", "Prospekt / Pilot"),
    CLIENT("client", "Kunde"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeAccountType>(CodeAccountType::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getAccountType(itemId: String): CodeAccountType? = getItem(itemId)
    }
}

