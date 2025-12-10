package io.zeitwert.fm.oe.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * User role enum using the NEW dddrive framework.
 */
enum class CodeUserRole(
    private val id: String,
    private val itemName: String,
) : Enumerated {
    // application admin (tenants, users, accounts)
    // login to kernel tenant only, without account
    APP_ADMIN("app_admin", "Application Admin"),
    // admin for a advisor or community tenant (1 tenant, n users, 1 .. n accounts)
    // login to advisor or community tenant, without account
    ADMIN("admin", "Tenant Admin"),
    // elevated user, needs account (so either in advisor or community tenant)
    SUPER_USER("super_user", "Super User"),
    // normal user, needs account (so either in advisor or community tenant)
    USER("user", "User"),
    // read-only user, needs account (so either in advisor or community tenant)
    READ_ONLY("read_only", "Read-Only User"),
    ;

    override fun getId() = id

    override fun getName() = itemName

    override fun getEnumeration() = Enumeration

    companion object Enumeration : EnumerationBase<CodeUserRole>(CodeUserRole::class.java) {
        init {
            entries.forEach { addItem(it) }
        }

        @JvmStatic
        fun getUserRole(itemId: String): CodeUserRole? = getItem(itemId)
    }
}

