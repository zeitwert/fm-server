package io.zeitwert.fm.account.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Client segment enum using the NEW dddrive framework.
 *
 * Values derived from: db/V1.0/3-config/R__1011_account_config.sql
 */
enum class CodeClientSegment(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    COMMUNITY("community", "Gemeinde"),
    FAMILY("family", "Family Office"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeClientSegment>(CodeClientSegment::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getClientSegment(itemId: String): CodeClientSegment? = getItem(itemId)
    }
}

