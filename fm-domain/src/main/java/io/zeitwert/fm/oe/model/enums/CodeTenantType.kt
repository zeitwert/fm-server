package io.zeitwert.fm.oe.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

enum class CodeTenantType(
	override val defaultName: String,
) : EnumeratedEnum {

	// Kernel Tenant (Nr 1)
	// placeholder to do application administration (tenants, users)
	KERNEL("Kernel"),

	// Container for 1 dedicated Account
	COMMUNITY("Gemeinde"),

	// An Advisor Tenant may contain multiple Accounts
	ADVISOR("Berater"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTenantType>(CodeTenantType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getTenantType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
