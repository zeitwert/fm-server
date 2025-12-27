package io.zeitwert.fm.oe.model

import dddrive.ddd.property.model.ReferenceSetProperty
import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.oe.model.enums.CodeUserRole

interface ObjUserFM : ObjUser {

	val isAppAdmin: Boolean

	val isAdmin: Boolean

	var needPasswordChange: Boolean?

	override var password: String?

	var avatarImageId: Any?

	val avatarImage: ObjDocument?

	var role: CodeUserRole?

	fun hasRole(role: CodeUserRole): Boolean

	val tenantSet: ReferenceSetProperty<ObjTenant>

}
