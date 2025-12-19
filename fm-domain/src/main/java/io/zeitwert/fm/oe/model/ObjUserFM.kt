package io.zeitwert.fm.oe.model

import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.oe.model.enums.CodeUserRole

interface ObjUserFM : ObjUser {

	val isAppAdmin: Boolean

	val isAdmin: Boolean

	var needPasswordChange: Boolean?

	override var password: String?

	val avatarImageId: Any?

	val avatarImage: ObjDocument?

	var role: CodeUserRole?

	fun hasRole(role: CodeUserRole): Boolean

	val tenantSet: Set<ObjTenant>

	fun clearTenantSet()

	fun addTenant(tenant: ObjTenant)

	fun removeTenant(tenant: ObjTenant)

}
