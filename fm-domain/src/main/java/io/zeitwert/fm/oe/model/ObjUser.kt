package io.zeitwert.fm.oe.model

import dddrive.app.obj.model.Obj
import dddrive.ddd.property.model.ReferenceSetProperty
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.oe.model.enums.CodeUserRole

interface ObjUser : Obj {

	var email: String?

	var password: String?

	var needPasswordChange: Boolean?

	var name: String?

	var description: String?

	val isAppAdmin: Boolean

	val isAdmin: Boolean

	var avatarImageId: Any?

	val avatarImage: ObjDocument?

	var role: CodeUserRole?

	fun hasRole(role: CodeUserRole): Boolean

	val tenantSet: ReferenceSetProperty<ObjTenant>

}
