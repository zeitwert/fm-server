package io.zeitwert.fm.building.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeHistoricPreservation(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    NONE("none", "Kein Denkmalschutz"),
    PARTIAL("partial", "Teilweise geschützt"),
    FULL("full", "Vollständig geschützt"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeHistoricPreservation>(CodeHistoricPreservation::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getHistoricPreservation(itemId: String): CodeHistoricPreservation? = getItem(itemId)
    }
}

