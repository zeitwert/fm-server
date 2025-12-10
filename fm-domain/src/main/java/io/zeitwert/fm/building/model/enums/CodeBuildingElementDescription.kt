package io.zeitwert.fm.building.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeBuildingElementDescription(
    private val id: String,
    private val itemName: String,
    val category: String,
) : Enumerated {
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeBuildingElementDescription>(CodeBuildingElementDescription::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getElementDescription(itemId: String): CodeBuildingElementDescription? = getItem(itemId)
    }
}

