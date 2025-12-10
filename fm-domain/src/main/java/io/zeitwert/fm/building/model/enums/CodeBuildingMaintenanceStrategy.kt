package io.zeitwert.fm.building.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeBuildingMaintenanceStrategy(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    M("M", "Minimal"),
    N("N", "Normal"),
    NW("NW", "Normal Wohlen"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeBuildingMaintenanceStrategy>(CodeBuildingMaintenanceStrategy::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getMaintenanceStrategy(itemId: String): CodeBuildingMaintenanceStrategy? = getItem(itemId)
    }
}

