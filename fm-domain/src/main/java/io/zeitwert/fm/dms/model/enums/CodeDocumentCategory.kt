package io.zeitwert.fm.dms.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeDocumentCategory(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    AVATAR("avatar", "Avatar"),
    BANNER("banner", "Banner"),
    FOTO("foto", "Foto"),
    LOGO("logo", "Logo"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeDocumentCategory>(CodeDocumentCategory::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getDocumentCategory(itemId: String): CodeDocumentCategory? = getItem(itemId)
    }
}

