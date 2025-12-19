package io.zeitwert.fm.oe.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeTenantType(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	// Kernel Tenant (Nr 1)
	// placeholder to do application administration (tenants, users)
	KERNEL("kernel", "Kernel"),

	// Container for 1 dedicated Account
	COMMUNITY("community", "Gemeinde"),

	// An Advisor Tenant may contain multiple Accounts
	ADVISOR("advisor", "Berater"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTenantType>(CodeTenantType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getTenantType(itemId: String): CodeTenantType? = getItem(itemId)
	}
}
