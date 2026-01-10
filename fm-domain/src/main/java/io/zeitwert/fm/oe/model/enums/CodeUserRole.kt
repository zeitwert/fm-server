package io.zeitwert.fm.oe.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

enum class CodeUserRole(
	override val defaultName: String,
) : EnumeratedEnum {

	// application admin (tenants, users, accounts)
	// login to kernel tenant only, without account
	APP_ADMIN("Application Admin"),

	// admin for a advisor or community tenant (1 tenant, n users, 1 .. n accounts)
	// login to advisor or community tenant, without account
	ADMIN("Tenant Admin"),

	// elevated user, needs account (so either in advisor or community tenant)
	SUPER_USER("Super User"),

	// normal user, needs account (so either in advisor or community tenant)
	USER("User"),

	// read-only user, needs account (so either in advisor or community tenant)
	READ_ONLY("Read-Only User"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeUserRole>(CodeUserRole::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getUserRole(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
